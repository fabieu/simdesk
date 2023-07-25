package de.sustineo.acc.leaderboard.configuration;

import de.sustineo.acc.leaderboard.utils.ApplicationContextProvider;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log
@Configuration
@Profile(ProfileManager.PROFILE_FILE_INTEGRATION)
public class FileContentConfiguration {
    private final ApplicationContextProvider applicationContextProvider;
    public static Set<Path> WATCH_DIRECTORIES = new HashSet<>();

    public FileContentConfiguration(ApplicationContextProvider applicationContextProvider) {
        this.applicationContextProvider = applicationContextProvider;
    }

    @Value("${leaderboard.results.folder}")
    public void setWatchDirectories(List<String> folderPaths) {
        validateResultsFolderPaths(folderPaths);

        FileContentConfiguration.WATCH_DIRECTORIES = folderPaths.stream()
                .map(Path::of)
                .collect(Collectors.toSet());
    }

    private void validateResultsFolderPaths(List<String> folders) {
        if (folders == null || folders.isEmpty()) {
            log.severe("No results folder configured. Please set a folder via 'LEADERBOARD_RESULTS_FOLDER' environment variable.");
            applicationContextProvider.exitApplication(1);
        } else {
            for (String folder : folders) {
                try {
                    Path folderPath = Path.of(folder);
                    if (!Files.isDirectory(folderPath)) {
                        log.severe(String.format("Configured results folder '%s' is not a directory.", folder));
                        applicationContextProvider.exitApplication(1);
                    }
                } catch (InvalidPathException e) {
                    log.severe(String.format("Configured results folder '%s' is not a valid path.", folder));
                    applicationContextProvider.exitApplication(1);
                }
            }
        }
    }
}
