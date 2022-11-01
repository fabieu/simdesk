package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.json.Session;
import de.sustineo.acc.leaderboard.utils.json.JsonUtils;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log
@Service
public class SessionService {

    @Scheduled(fixedDelay = 1000)
    private void processSessions() {
        List<Path> paths = new ArrayList<>();

        for (Path path : paths) {
            log.info("Importing file " + path.toAbsolutePath());
            Optional<Session> session = readSession(path);

            if (session.isPresent()) {
                // TODO: Convert and insert
            }
        }
    }

    public Optional<Session> readSession(Path path) {
        try {
            String fileContent = Files.readString(path, StandardCharsets.UTF_16LE);
            return Optional.ofNullable(JsonUtils.fromJson(fileContent, Session.class));
        } catch (IOException e) {
            log.severe(e.getMessage());
            return Optional.empty();
        }
    }
}
