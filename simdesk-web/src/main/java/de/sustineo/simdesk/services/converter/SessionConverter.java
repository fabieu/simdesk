package de.sustineo.simdesk.services.converter;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.Simulation;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(SpringProfile.LEADERBOARD)
@Service
public class SessionConverter extends BaseConverter {
    public Session convertToSession(AccSession accSession, String fileContent, FileMetadata fileMetadata) {
        return Session.builder()
                .sessionType(accSession.getSessionType())
                .raceWeekendIndex(accSession.getRaceWeekendIndex())
                .serverName(accSession.getServerName())
                .simulationId(Simulation.ACC.getId())
                .trackId(accSession.getTrackName())
                .wetSession(accSession.getSessionResult().getIsWetSession())
                .carCount(accSession.getSessionResult().getLeaderboardLines().size())
                .sessionDatetime(fileMetadata.getModifiedDatetime())
                .fileChecksum(fileMetadata.getChecksum())
                .fileName(fileMetadata.getName())
                .fileContent(fileContent)
                .build();
    }
}
