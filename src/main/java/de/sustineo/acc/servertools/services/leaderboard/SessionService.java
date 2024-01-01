package de.sustineo.acc.servertools.services.leaderboard;

import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.entities.FileMetadata;
import de.sustineo.acc.servertools.entities.Session;
import de.sustineo.acc.servertools.entities.json.AccSession;
import de.sustineo.acc.servertools.entities.mapper.SessionMapper;
import de.sustineo.acc.servertools.services.converter.SessionConverter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    private List<Pattern> ignorePatterns;

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

    @Value("${leaderboard.results.ignore-patterns}")
    private void setIgnorePatterns(String ignorePatterns) {
        if (ignorePatterns == null || ignorePatterns.isBlank()) {
            return;
        }

        List<String> splitIgnorePatterns = List.of(ignorePatterns.split("\\|"));

        this.ignorePatterns = splitIgnorePatterns.stream()
                .map(Pattern::compile)
                .toList();
    }

    public List<Session> getAllSessions() {
        return sessionMapper.findAll();
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

    public List<Session> getRecentSessions(int recentDays) {
        Instant untilDatetime = Instant.now().minus(recentDays, ChronoUnit.DAYS);
        return sessionMapper.findRecentSessions(untilDatetime);
    }

    @Transactional
    public void handleSession(AccSession accSession, FileMetadata fileMetadata) {
        // Ignore session without any laps
        if (accSession.getLaps().isEmpty()) {
            log.info(String.format("Ignoring session %s because it has no laps", fileMetadata.getFile()));
            return;
        }

        // Ignore session based on specific characters in server name
        if (accSession.getServerName() != null && ignorePatterns != null) {
            for (Pattern ignorePattern : ignorePatterns) {
                if (ignorePattern.matcher(accSession.getServerName()).find()) {
                    log.info(String.format("Ignoring session %s because server name '%s' matches ignore pattern '%s'", fileMetadata.getFile(), accSession.getServerName(), ignorePattern));
                    return;
                }
            }
        }

        Session session = sessionConverter.convertToSession(accSession, fileMetadata);
        if (sessionExists(session.getFileChecksum())) {
            log.info(String.format("Ignoring session %s because it already exists", fileMetadata.getFile()));
            return;
        }

        // Insert session first to get the id (auto increment)
        sessionMapper.insert(session);

        // Actual processing of the session results
        leaderboardService.handleLeaderboard(session.getId(), accSession, fileMetadata);
        lapService.handleLaps(session.getId(), accSession, fileMetadata);
        penaltyService.handlePenalties(session.getId(), accSession);

        log.info(String.format("Successfully processed session file %s", fileMetadata.getFile()));
    }
}
