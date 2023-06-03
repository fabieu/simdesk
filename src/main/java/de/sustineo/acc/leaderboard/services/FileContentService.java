package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.configuration.FileContentConfiguration;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import de.sustineo.acc.leaderboard.utils.json.JsonUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;

@Log
@Service
public class FileContentService {
    private final WatchService watchService;
    private final List<Charset> SUPPORTED_CHARSETS = List.of(StandardCharsets.UTF_8, StandardCharsets.UTF_16LE);

    @Autowired
    public FileContentService(WatchService watchService) {
        this.watchService = watchService;
    }

    @Async
    @PostConstruct
    public void startMonitoringFolders() {
        try {
            WatchKey key;
            while ((key = watchService.take()) != null) {
                Path dir = FileContentConfiguration.watchKeyMap.get(key);

                for (WatchEvent<?> event : key.pollEvents()) {
                    Path relativePath = (Path) event.context();
                    Path fileName = dir.resolve(relativePath);

                    if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
                        log.fine(String.format("Processing event kind: %s; File affected: %s", event.kind(), fileName));
                        handleSessionFile(fileName);
                    } else {
                        log.fine(String.format("Ignoring event kind: %s; File affected: %s", event.kind(), fileName));
                    }
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            log.warning("interrupted exception for monitoring service");
        }
    }

    @PreDestroy
    public void stopMonitoringFolders() {
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                log.severe("exception while closing the monitoring service");
            }
        }
    }

    public void handleSessionFile(Path path) {
        try {
            String fileContent = readFile(path);
            AccSession accSession = JsonUtils.fromJson(fileContent, AccSession.class);
            log.fine(accSession.toString());
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private String readFile(Path path) throws IOException {
        for (Charset charset : SUPPORTED_CHARSETS) {
            try {
                String fileContent = Files.readString(path, charset);
                fileContent = removeBOM(fileContent);
                log.fine(String.format("Successfully parsed %s with charset %s", path, charset));

                return fileContent;
            } catch (IOException e) {
                log.fine(String.format("Unable to parse %s with charset %s", path, charset));
            }
        }

        throw new IOException(String.format("Could not parse %s with any the supported charsets: %s", path.toAbsolutePath(), SUPPORTED_CHARSETS));
    }

    public String removeBOM(String s) {
        final String BOM_MARKER = "\uFEFF";

        if (s.startsWith(BOM_MARKER)) {
            return s.substring(1);
        } else {
            return s;
        }
    }

    public String getChecksum(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            messageDigest.update(byteArray, 0, bytesCount);
        }
        ;

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = messageDigest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
}
