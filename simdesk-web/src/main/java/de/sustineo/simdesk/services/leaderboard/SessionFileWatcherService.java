package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.FileContentConfiguration;
import de.sustineo.simdesk.configuration.ProfileManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Set;
import java.util.logging.Level;

@Log
@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class SessionFileWatcherService {
    private final SessionFileService sessionFileService;
    private final FileAlterationMonitor monitor;

    @Autowired
    public SessionFileWatcherService(SessionFileService sessionFileService,
                                     @Value("${simdesk.results.scan-interval}") String scanInterval) {
        this.sessionFileService = sessionFileService;
        this.monitor = new FileAlterationMonitor(Duration.parse(scanInterval).toMillis());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startFileAlterationMonitor() {
        Set<Path> watchDirectories = FileContentConfiguration.WATCH_DIRECTORIES;

        try {
            for (Path path : watchDirectories) {
                FileAlterationObserver observer = FileAlterationObserver.builder()
                        .setFile(path.toFile())
                        .get();

                FileAlterationListener listener = new FileAlterationListenerAdaptor() {
                    @Override
                    public void onFileCreate(File file) {
                        log.fine(String.format("Processing file create event; File affected: %s", file.getAbsolutePath()));
                        sessionFileService.handleSessionFile(file.toPath().toAbsolutePath());
                    }

                    @Override
                    public void onFileChange(File file) {
                        log.fine(String.format("Processing file change event; File affected: %s", file.getAbsolutePath()));
                        sessionFileService.handleSessionFile(file.toPath().toAbsolutePath());
                    }

                    @Override
                    public void onFileDelete(File file) {
                        log.fine(String.format("Ignoring file delete event; File affected: %s", file.getAbsolutePath()));
                    }
                };
                observer.addListener(listener);
                monitor.addObserver(observer);
            }

            monitor.start();
        } catch (Exception e) {
            log.log(Level.SEVERE, "exception while creating the monitoring service", e);
        }
    }

    @PreDestroy
    public void stopWatchService() {
        if (monitor != null) {
            try {
                monitor.stop();
            } catch (Exception e) {
                log.log(Level.SEVERE, "exception while closing the monitoring service", e);
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void readAllSessionFiles() {
        Set<Path> watchDirectories = FileContentConfiguration.WATCH_DIRECTORIES;

        for (Path watchDirectory : watchDirectories) {
            FileUtils.listFiles(watchDirectory.toFile(), new String[]{"json"}, true)
                    .forEach(file -> sessionFileService.handleSessionFile(file.toPath()));
        }
    }
}
