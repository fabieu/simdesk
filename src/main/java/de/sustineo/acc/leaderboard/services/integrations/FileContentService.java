package de.sustineo.acc.leaderboard.services.integrations;

import de.sustineo.acc.leaderboard.configuration.FileContentConfiguration;
import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import de.sustineo.acc.leaderboard.services.FileService;
import de.sustineo.acc.leaderboard.services.SessionService;
import de.sustineo.acc.leaderboard.utils.json.JsonUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.stream.Stream;

@Profile("file-integration")
@Log
@Service
public class FileContentService {
    private final WatchService watchService;
    private final SessionService sessionService;
    private final FileService fileService;
    private final List<Charset> SUPPORTED_CHARSETS = List.of(StandardCharsets.UTF_8, StandardCharsets.UTF_16LE);

    @Autowired
    public FileContentService(WatchService watchService, SessionService sessionService, FileService fileService) {
        this.watchService = watchService;
        this.sessionService = sessionService;
        this.fileService = fileService;
    }

    @PostConstruct
    public void createWatchServiceThreadPool() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
        executor.submit(this::startWatchService);
    }

    public void startWatchService() {
        try {
            WatchKey key;
            while ((key = watchService.take()) != null) {
                Path baseDirectory = FileContentConfiguration.watchKeyMap.get(key);

                for (WatchEvent<?> event : key.pollEvents()) {
                    Path relativePath = (Path) event.context();
                    Path absolutePath = baseDirectory.resolve(relativePath);

                    if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind()) && FileService.isSessionFile(absolutePath)) {
                        log.fine(String.format("Processing event kind: %s; File affected: %s", event.kind(), absolutePath));
                        handleSessionFile(absolutePath);
                    } else {
                        log.fine(String.format("Ignoring event kind: %s; File affected: %s", event.kind(), absolutePath));
                    }
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            log.warning("interrupted exception for monitoring service");
        }
    }

    @PreDestroy
    public void stopWatchService() {
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                log.severe("exception while closing the monitoring service");
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void readAllSessionFiles() throws IOException {
        Set<Path> watchDirectories = FileContentConfiguration.WATCH_DIRECTORIES;

        for (Path watchDirectory : watchDirectories) {
            try (Stream<Path> pathStream = Files.walk(watchDirectory)) {
                pathStream
                        .filter(Files::isRegularFile)
                        .filter(FileService::isSessionFile)
                        .forEach(this::handleSessionFile);
            }
        }
    }


    public void handleSessionFile(Path file) {
        try {
            String fileContent = readFile(file);
            AccSession accSession = JsonUtils.fromJson(fileContent, AccSession.class);
            FileMetadata fileMetadata = new FileMetadata(file);

            sessionService.handleSession(accSession, fileMetadata);
            log.info(String.format("Successfully processed session file %s", file));
        } catch (IOException e) {
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
