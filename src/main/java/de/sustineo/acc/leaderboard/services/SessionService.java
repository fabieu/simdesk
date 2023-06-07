package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.Lap;
import de.sustineo.acc.leaderboard.entities.Session;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import de.sustineo.acc.leaderboard.entities.mapper.LapMapper;
import de.sustineo.acc.leaderboard.entities.mapper.SessionMapper;
import de.sustineo.acc.leaderboard.services.converter.SessionConverter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log
@Service
public class SessionService {
    private final SessionConverter sessionConverter;
    private final SessionMapper sessionMapper;
    private final LapMapper lapMapper;

    @Autowired
    public SessionService(SessionConverter sessionConverter, SessionMapper sessionMapper, LapMapper lapMapper) {
        this.sessionConverter = sessionConverter;
        this.sessionMapper = sessionMapper;
        this.lapMapper = lapMapper;
    }

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

        persistSession(session);
        List<Lap> laps = sessionConverter.convertToLaps(session.getId(), accSession);
        laps.forEach(this::persistLap);
    }

    private void persistLap(Lap lap) {
        lapMapper.insert(lap);
    }

    private void persistSession(Session session) {
        sessionMapper.insert(session);
    }

    private boolean sessionExists(Session session) {
        return sessionMapper.findByFileChecksum(session.getFileChecksum()) != null;
    }
}
