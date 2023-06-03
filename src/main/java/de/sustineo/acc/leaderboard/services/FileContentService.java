package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.configuration.FileContentConfiguration;
import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import de.sustineo.acc.leaderboard.utils.json.JsonUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;

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

                    if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
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

    public void handleSessionFile(Path filePath) {
        try {
            String fileContent = readFile(filePath);
            AccSession accSession = JsonUtils.fromJson(fileContent, AccSession.class);
            FileMetadata fileMetadata = new FileMetadata(filePath);

            sessionService.handleSession(accSession, fileMetadata);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private String readFile(Path path) throws IOException {
        for (Charset charset : SUPPORTED_CHARSETS) {
            try {
                String fileContent = Files.readString(path, charset);
                fileContent = fileService.removeBOM(fileContent);
                log.fine(String.format("Successfully parsed %s with charset %s", path, charset));

                return fileContent;
            } catch (IOException e) {
                log.fine(String.format("Unable to parse %s with charset %s", path, charset));
            }
        }

        throw new IOException(String.format("Could not parse %s with any the supported charsets: %s", path.toAbsolutePath(), SUPPORTED_CHARSETS));
    }
}
