package de.sustineo.acc.leaderboard.configuration;

import de.sustineo.acc.leaderboard.services.FileService;
import de.sustineo.acc.leaderboard.utils.ApplicationContextProvider;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Log
@Configuration
@Profile("file-integration")
public class FileContentConfiguration {
    private final ApplicationContextProvider applicationContextProvider;
    private final FileService fileService;
    public static Set<Path> WATCH_DIRECTORIES = new HashSet<>();
    public static final Map<WatchKey, Path> watchKeyMap = new HashMap<>();

    public FileContentConfiguration(ApplicationContextProvider applicationContextProvider, FileService fileService) {
        this.applicationContextProvider = applicationContextProvider;
        this.fileService = fileService;
    }

    @Value("${leaderboard.results.folder}")
    public void setWatchDirectories(List<String> folderPaths) {
        if (folderPaths == null || folderPaths.isEmpty()) {
            log.severe("No results folder configured. Please set a folder via 'LEADERBOARD_RESULTS_FOLDER' environment variable.");
            applicationContextProvider.exitApplication(1);
        } else {
            for (String folderPath :  folderPaths){
                Set<Path> directories = fileService.listDirectories(Path.of(folderPath));
                FileContentConfiguration.WATCH_DIRECTORIES.addAll(directories);
            }
        }
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
            for (Path path : WATCH_DIRECTORIES) {

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
