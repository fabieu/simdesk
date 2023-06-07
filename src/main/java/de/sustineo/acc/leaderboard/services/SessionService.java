package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.Lap;
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
    private final SessionConverter sessionConverter;
    private final SessionMapper sessionMapper;

    @Autowired
    public SessionService(SessionConverter sessionConverter, SessionMapper sessionMapper) {
        this.sessionConverter = sessionConverter;
        this.sessionMapper = sessionMapper;
    }

    public void handleSession(AccSession accSession, FileMetadata fileMetadata) {
        if (accSession.getLaps().isEmpty()) {
            log.info(String.format("Ignoring session %s because it has no laps", fileMetadata.getAbsolutePath()));
            return;
        }

        Session session = sessionConverter.convertToSession(accSession, fileMetadata);
        //persistSession(session);

        List<Lap> laps = sessionConverter.convertToLaps(session.getId(), accSession);
        //laps.forEach(this::persistLap);

        log.fine(String.format("Successfully processed session %s with %s laps", fileMetadata.getName(), laps.size()));
    }

    private void persistLap(Lap lap) {
        sessionMapper.insertLap(lap);
    }

    private void persistSession(Session session) {
        sessionMapper.insertSession(session);
    }
}
