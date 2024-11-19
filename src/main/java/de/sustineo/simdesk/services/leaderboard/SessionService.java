package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.entities.mapper.SessionMapper;
import de.sustineo.simdesk.services.converter.SessionConverter;
import de.sustineo.simdesk.views.enums.TimeRange;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Log
@Service
public class SessionService {
    private final SessionConverter sessionConverter;
    private final SessionMapper sessionMapper;
    private final LapService lapService;
    private final LeaderboardService leaderboardService;
    private final PenaltyService penaltyService;
    private Pattern ignorePattern;

    @Autowired
    public SessionService(SessionConverter sessionConverter,
                          SessionMapper sessionMapper,
                          LapService lapService,
                          LeaderboardService leaderboardService,
                          PenaltyService penaltyService) {
        this.sessionConverter = sessionConverter;
        this.sessionMapper = sessionMapper;
        this.lapService = lapService;
        this.leaderboardService = leaderboardService;
        this.penaltyService = penaltyService;
    }

    @Value("${simdesk.results.exclude-pattern}")
    private void setIgnorePattern(String ignorePattern) {
        if (ignorePattern == null || ignorePattern.isBlank()) {
            return;
        }

        this.ignorePattern = Pattern.compile(ignorePattern);
    }

    public List<Session> getAllSessions(TimeRange timeRange) {
        return sessionMapper.findAllByTimeRange(timeRange.start(), timeRange.end());
    }

    public Session getSession(String fileChecksum) {
        return sessionMapper.findByFileChecksum(fileChecksum);
    }

    public boolean sessionExists(String fileChecksum) {
        return getSession(fileChecksum) != null;
    }

    public long getSessionCount() {
        return sessionMapper.count();
    }

    @Transactional
    public void handleSession(AccSession accSession, String fileContent, FileMetadata fileMetadata) {
        String fileName = fileMetadata.getFile().toString();

        // Ignore session without any laps
        if (accSession.getLaps().isEmpty()) {
            log.info(String.format("Ignoring session file %s because it has no laps", fileName));
            return;
        }

        // Ignore session based on specific pattern in server name
        if (accSession.getServerName() != null && ignorePattern != null) {
            if (ignorePattern.matcher(accSession.getServerName()).find()) {
                log.info(String.format("Ignoring session file %s because server name '%s' matches ignore pattern '%s'", fileName, accSession.getServerName(), ignorePattern));
                return;
            }
        }

        Session session = sessionConverter.convertToSession(accSession, fileContent, fileMetadata);
        if (sessionExists(session.getFileChecksum())) {
            log.info(String.format("Ignoring session file %s because it already exists", fileName));
            return;
        }

        // Insert session first to get the id (auto increment)
        sessionMapper.insert(session);

        // Actual processing of the session results
        leaderboardService.handleLeaderboard(session.getId(), accSession, fileMetadata);
        lapService.handleLaps(session.getId(), accSession, fileMetadata);
        penaltyService.handlePenalties(session.getId(), accSession);

        log.info(String.format("Successfully processed session file %s", fileName));
    }
}
