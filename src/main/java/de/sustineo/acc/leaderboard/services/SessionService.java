package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.Session;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import de.sustineo.acc.leaderboard.entities.mapper.SessionMapper;
import de.sustineo.acc.leaderboard.services.converter.SessionConverter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log
@Service
public class SessionService {
    private final LapService lapService;
    private final LeaderboardService leaderboardService;
    private final SessionConverter sessionConverter;
    private final SessionMapper sessionMapper;

    @Autowired
    public SessionService(SessionConverter sessionConverter, SessionMapper sessionMapper, LapService lapService, LeaderboardService leaderboardService) {
        this.sessionConverter = sessionConverter;
        this.sessionMapper = sessionMapper;
        this.lapService = lapService;
        this.leaderboardService = leaderboardService;
    }

    public boolean sessionExists(Integer sessionId) {
        return sessionMapper.findById(sessionId) != null;
    }

    public List<Session> getAllSessions() {
        return sessionMapper.findAll();
    }

    public void handleSession(AccSession accSession, FileMetadata fileMetadata) {
        if (accSession.getLaps().isEmpty()) {
            log.info(String.format("Ignoring session %s because it has no laps", fileMetadata.getFile()));
            return;
        }

        Session session = sessionConverter.convertToSession(accSession, fileMetadata);
        if (sessionImported(session)){
            log.info(String.format("Ignoring session %s because it already exists", fileMetadata.getFile()));
            return;
        }

        // Insert session first to get the id (auto increment)
        sessionMapper.insert(session);

        // Actual processing of the session results
        leaderboardService.handleLeaderboard(session.getId(), accSession, fileMetadata);
        lapService.handleLaps(session.getId(), accSession, fileMetadata);

        // Mark session as successfully imported
        sessionMapper.setImportSuccess(session);
    }

    private boolean sessionImported(Session session) {
        Session existingSession = sessionMapper.findByFileChecksum(session.getFileChecksum());

        if (existingSession == null) {
            return false;
        }

        return existingSession.getImportSuccess();
    }
}
