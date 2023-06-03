package de.sustineo.acc.leaderboard.services.converter;

import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.LeaderboardLine;
import de.sustineo.acc.leaderboard.entities.Session;
import de.sustineo.acc.leaderboard.entities.json.AccLeaderboardLine;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionConverter {
    public Session convertToSession(AccSession accSession, FileMetadata fileMetadata) {
        return Session.builder()
                .sessionType(accSession.getSessionType())
                .raceWeekendIndex(accSession.getRaceWeekendIndex())
                .serverName(accSession.getServerName())
                .wetSession(accSession.getSessionResult().getIsWetSession())
                .driverCount(accSession.getSessionResult().getLeaderboardLines().size())
                .fileChecksum(fileMetadata.getChecksum())
                .fileName(fileMetadata.getName())
                .fileDirectory(fileMetadata.getDirectory().toString())
                .build();
    }

    public List<LeaderboardLine> convertToLeaderboardLines(AccSession accSession, FileMetadata fileMetadata) {
        List<AccLeaderboardLine> leaderboardLines = accSession.getSessionResult().getLeaderboardLines();

        return leaderboardLines.stream()
                .map(accLeaderboardLine -> convertToLeaderboardLine(accLeaderboardLine, accSession, fileMetadata))
                .collect(Collectors.toList());
    }

    private LeaderboardLine convertToLeaderboardLine(AccLeaderboardLine accLeaderboardLine, AccSession accSession, FileMetadata fileMetadata) {
        return LeaderboardLine.builder()
                .build();
    }
}
