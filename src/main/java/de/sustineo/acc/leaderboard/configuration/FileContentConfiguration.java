package de.sustineo.acc.leaderboard.configuration;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
@Configuration
@Getter
public class FileContentConfiguration {
    public static List<String> WATCH_DIRECTORIES;
    public static final Map<WatchKey, Path> watchKeyMap = new HashMap<>();

    @Value("${leaderboard.results.folder}")
    public void setWatchDirectories(List<String> folderPaths) {
        FileContentConfiguration.WATCH_DIRECTORIES = folderPaths;
    }

    public List<Path> getRegisteredWatchDirectories() {
        return watchKeyMap.values()
                .stream()
                .toList();
    }

    @Bean
    public WatchService watchService() throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();

        if (WATCH_DIRECTORIES != null) {
            for (String watchDirectory : WATCH_DIRECTORIES) {
                Path path = Paths.get(watchDirectory);

                if (!Files.isDirectory(path)) {
                    throw new RuntimeException("Incorrect monitoring folder: " + path);
                }

                WatchKey watchKey = path.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE
                );
                watchKeyMap.put(watchKey, path);
            }

            log.info(String.format("Monitoring following folders: %s", getRegisteredWatchDirectories()));
        }

        return watchService;
    }
}
