package de.sustineo.simdesk.services.converter;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.json.kunos.AccSession;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
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
