package de.sustineo.simdesk.configuration;

import de.sustineo.simdesk.utils.ApplicationContextProvider;
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
@Profile(ProfileManager.PROFILE_LEADERBOARD)
public class FileContentConfiguration {
    private final ApplicationContextProvider applicationContextProvider;
    public static Set<Path> WATCH_DIRECTORIES = new HashSet<>();

    public FileContentConfiguration(ApplicationContextProvider applicationContextProvider) {
        this.applicationContextProvider = applicationContextProvider;
    }

    @Value("${simdesk.results.folder}")
    public void setWatchDirectories(List<String> folderPaths) {
        validateResultsFolderPaths(folderPaths);

        FileContentConfiguration.WATCH_DIRECTORIES = folderPaths.stream()
                .map(Path::of)
                .collect(Collectors.toSet());
    }

    private void validateResultsFolderPaths(List<String> folders) {
        if (folders == null || folders.isEmpty()) {
            log.severe("No results folder configured. Please set a folder via 'SIMDESK_ACC_RESULTS_FOLDER' environment variable.");
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
