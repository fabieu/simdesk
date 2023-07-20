package de.sustineo.acc.leaderboard.services.integrations;

import de.sustineo.acc.leaderboard.configuration.FileContentConfiguration;
import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import de.sustineo.acc.leaderboard.services.FileService;
import de.sustineo.acc.leaderboard.services.SessionService;
import de.sustineo.acc.leaderboard.utils.json.JsonUtils;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Stream;

@Profile("file-integration")
@Log
@Service
public class FileContentService {
    private final List<Charset> SUPPORTED_CHARSETS = List.of(StandardCharsets.UTF_8, StandardCharsets.UTF_16LE);

    private final SessionService sessionService;
    private final FileService fileService;
    private final JsonUtils jsonUtils;

    private final FileAlterationMonitor monitor;



    @Autowired
    public FileContentService(SessionService sessionService, FileService fileService, JsonUtils jsonUtils, @Value("${leaderboard.results.scan_interval}") String scanInterval) {
        this.sessionService = sessionService;
        this.fileService = fileService;
        this.jsonUtils = jsonUtils;
        this.monitor = new FileAlterationMonitor(Duration.parse(scanInterval).toMillis());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startFileAlterationMonitor() {
        Set<Path> watchDirectories = FileContentConfiguration.WATCH_DIRECTORIES;

        try {
            for (Path path : watchDirectories) {
                FileAlterationObserver observer = new FileAlterationObserver(path.toFile());
                FileAlterationListener listener = new FileAlterationListenerAdaptor(){
                    @Override
                    public void onFileCreate(File file) {
                        log.fine(String.format("Processing file create event; File affected: %s", file.getAbsolutePath()));
                        handleSessionFile(file.toPath().toAbsolutePath());
                    }

                    @Override
                    public void onFileDelete(File file) {
                        log.fine(String.format("Ignoring file delete event; File affected: %s", file.getAbsolutePath()));
                    }

                    @Override
                    public void onFileChange(File file) {
                        log.fine(String.format("Ignoring file change event; File affected: %s", file.getAbsolutePath()));
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
                log.severe("exception while closing the monitoring service");
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void readAllSessionFiles() throws IOException {
        Set<Path> watchDirectories = FileContentConfiguration.WATCH_DIRECTORIES;

        for (Path watchDirectory : watchDirectories) {
            try (Stream<Path> pathStream = Files.list(watchDirectory)) {
                pathStream.forEach(this::handleSessionFile);
            }
        }
    }

    @Async
    public void handleSessionFile(Path file) {
        try {
            if (!Files.isRegularFile(file) || !FileService.isSessionFile(file)) {
                log.warning(String.format("Ignoring file %s because it is not a valid session file", file));
                return;
            }

            String fileContent = readFile(file);
            AccSession accSession = jsonUtils.fromJson(fileContent, AccSession.class);
            FileMetadata fileMetadata = new FileMetadata(file);

            sessionService.handleSession(accSession, fileMetadata);
        } catch (Exception e) {
            log.log(Level.SEVERE, String.format("Could not process session file %s", file), e);
        }
    }

    private String readFile(Path file) throws IOException {
        for (Charset charset : SUPPORTED_CHARSETS) {
            try {
                String fileContent = Files.readString(file, charset);
                fileContent = fileService.removeBOM(fileContent);
                fileContent = fileService.removeControlCharacters(fileContent);
                log.fine(String.format("Successfully parsed %s with charset %s", file, charset));

                return fileContent;
            } catch (IOException e) {
                log.fine(String.format("Unable to parse %s with charset %s", file, charset));
            }
        }

        throw new IOException(String.format("Could not parse %s with any the supported charsets: %s", file, SUPPORTED_CHARSETS));
    }
}
