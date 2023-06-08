package de.sustineo.acc.leaderboard.services.converter;

import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.Session;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import org.springframework.stereotype.Service;

@Service
public class SessionConverter {
    public Session convertToSession(AccSession accSession, FileMetadata fileMetadata) {
        return Session.builder()
                .sessionType(accSession.getSessionType())
                .raceWeekendIndex(accSession.getRaceWeekendIndex())
                .serverName(accSession.getServerName())
                .trackId(accSession.getTrackName())
                .wetSession(accSession.getSessionResult().getIsWetSession())
                .driverCount(accSession.getSessionResult().getLeaderboardLines().size())
                .fileChecksum(fileMetadata.getChecksum())
                .fileName(fileMetadata.getName())
                .fileDirectory(fileMetadata.getDirectory().toString())
                .build();
    }
}
