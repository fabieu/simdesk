package de.sustineo.acc.leaderboard.configuration;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    @Value("${leaderboard.results.folder}")
    private List<String> folderPaths;
    public static final Map<WatchKey, Path> watchKeyMap = new HashMap<>();

    @Bean
    @ConditionalOnProperty(value = "leaderboard.results.folder")
    public WatchService watchService() throws IOException {
        log.info(String.format("Monitoring following folders: %s", folderPaths));

        WatchService watchService = FileSystems.getDefault().newWatchService();

        for (String folderPath : folderPaths) {
            Path path = Paths.get(folderPath);

            if (!Files.isDirectory(path)) {
                throw new RuntimeException("incorrect monitoring folder: " + path);
            }

            WatchKey watchKey = path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE
            );
            watchKeyMap.put(watchKey, path);
        }

        return watchService;
    }
}
