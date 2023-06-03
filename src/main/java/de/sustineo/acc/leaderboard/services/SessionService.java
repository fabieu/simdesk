package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.LeaderboardLine;
import de.sustineo.acc.leaderboard.entities.Session;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import de.sustineo.acc.leaderboard.services.converter.SessionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {
    private final SessionConverter sessionConverter;

    @Autowired
    public SessionService(SessionConverter sessionConverter) {
        this.sessionConverter = sessionConverter;
    }

    public void handleSession(AccSession accSession, FileMetadata fileMetadata) {
        Session session = sessionConverter.convertToSession(accSession, fileMetadata);
        List<LeaderboardLine> leaderboardLine = sessionConverter.convertToLeaderboardLines(accSession, fileMetadata);
    }
}
