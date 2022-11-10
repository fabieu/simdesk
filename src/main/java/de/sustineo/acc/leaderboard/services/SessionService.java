package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.json.AccSession;
import de.sustineo.acc.leaderboard.entities.mapper.SessionMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log
@Service
public class SessionService {

    private final ContentService contentService;
    private final SessionMapper sessionMapper;

    @Autowired
    public SessionService(ContentService contentService, SessionMapper sessionMapper) {
        this.contentService = contentService;
        this.sessionMapper = sessionMapper;
    }

    @Scheduled(fixedDelayString = "${update.rate:15}", timeUnit = TimeUnit.SECONDS)
    private void processSessions() {
        List<Path> paths = List.of(Path.of("src/main/resources/examples/221008_224809_R.json"));

    }

    public List<AccSession> getSessions() {
        //List<Session> sessionList = sessionMapper.findAll();
        List<AccSession> sessionFilesList = contentService.getSessions();
        return sessionFilesList;
    }
}
