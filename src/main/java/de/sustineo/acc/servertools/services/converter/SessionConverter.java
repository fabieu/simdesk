package de.sustineo.acc.servertools.services.converter;

import de.sustineo.acc.servertools.entities.FileMetadata;
import de.sustineo.acc.servertools.entities.Session;
import de.sustineo.acc.servertools.entities.json.AccSession;
import org.springframework.stereotype.Service;

@Service
public class SessionConverter extends BaseConverter {
    public Session convertToSession(AccSession accSession, FileMetadata fileMetadata) {
        return Session.builder()
                .sessionType(accSession.getSessionType())
                .raceWeekendIndex(accSession.getRaceWeekendIndex())
                .serverName(accSession.getServerName())
                .trackId(accSession.getTrackName())
                .wetSession(accSession.getSessionResult().getIsWetSession())
                .carCount(accSession.getSessionResult().getLeaderboardLines().size())
                .sessionDatetime(fileMetadata.getModifiedDatetime())
                .fileChecksum(fileMetadata.getChecksum())
                .fileName(fileMetadata.getName())
                .fileDirectory(fileMetadata.getDirectory().toString())
                .build();
    }
}
