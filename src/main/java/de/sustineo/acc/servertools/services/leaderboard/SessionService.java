package de.sustineo.acc.servertools.services.leaderboard;

import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.entities.FileMetadata;
import de.sustineo.acc.servertools.entities.Session;
import de.sustineo.acc.servertools.entities.json.AccSession;
import de.sustineo.acc.servertools.entities.mapper.SessionMapper;
import de.sustineo.acc.servertools.services.converter.SessionConverter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
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

    public Session getSession(Integer sessionId) {
        return sessionMapper.findById(sessionId);
    }

    @Transactional
    public void handleSession(AccSession accSession, FileMetadata fileMetadata) {
        if (accSession.getLaps().isEmpty()) {
            log.info(String.format("Ignoring session %s because it has no laps", fileMetadata.getFile()));
            return;
        }

        Session session = sessionConverter.convertToSession(accSession, fileMetadata);
        if (sessionExists(session)){
            log.info(String.format("Ignoring session %s because it already exists", fileMetadata.getFile()));
            return;
        }

        // Insert session first to get the id (auto increment)
        sessionMapper.insert(session);

        // Actual processing of the session results
        leaderboardService.handleLeaderboard(session.getId(), accSession, fileMetadata);
        lapService.handleLaps(session.getId(), accSession, fileMetadata);

        log.info(String.format("Successfully processed session file %s", fileMetadata.getFile()));
    }

    private boolean sessionExists(Session session) {
        Session existingSession = sessionMapper.findByFileChecksum(session.getFileChecksum());
        return existingSession != null;
    }
}
