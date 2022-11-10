package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.json.AccSession;
import de.sustineo.acc.leaderboard.utils.json.JsonUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Log
@Service
public class FileContentService implements ContentService {
    private final static String ROOT_FILE_PATH = "src/main/resources/examples";
    private final FileService fileService;

    @Autowired
    public FileContentService(FileService fileService) {
        this.fileService = fileService;
    }

    public List<AccSession> getSessions() {
        try (Stream<Path> walkStream = Files.walk(Paths.get(ROOT_FILE_PATH))) {
            return walkStream
                    .filter(path -> path.toFile().isFile())
                    .filter(path -> path.getFileName().toString().matches(".*"))
                    .map(this::readSession)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            log.severe(e.getMessage());
            return Collections.emptyList();
        }
    }

    public AccSession readSession(Path path) {
        try {
            String fileContent = Files.readString(path, StandardCharsets.UTF_16LE);
            fileContent = fileService.removeBOM(fileContent);
            return JsonUtils.fromJson(fileContent, AccSession.class);
        } catch (IOException e) {
            log.severe(e.getMessage());
            return null;
        }
    }
}
