package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.CacheNames;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.mybatis.mapper.SessionMapper;
import de.sustineo.simdesk.services.converter.SessionConverter;
import de.sustineo.simdesk.views.enums.TimeRange;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

@Profile(SpringProfile.LEADERBOARD)
@Log
@Service
public class SessionService {
    private final SessionService self;
    private final SessionConverter sessionConverter;
    private final SessionMapper sessionMapper;
    private final LapService lapService;
    private final LeaderboardService leaderboardService;
    private final PenaltyService penaltyService;

    private Pattern ignorePattern;

    @Autowired
    public SessionService(@Lazy SessionService self,
                          SessionConverter sessionConverter,
                          SessionMapper sessionMapper,
                          LapService lapService,
                          LeaderboardService leaderboardService,
                          PenaltyService penaltyService) {
        this.self = self;
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

    public List<Session> getAllBySessionTimeRange(TimeRange timeRange) {
        return self.getAllBySessionTimeRange(timeRange.from(), timeRange.to());
    }

    @Cacheable(cacheNames = CacheNames.SESSIONS)
    public List<Session> getAllBySessionTimeRange(Instant from, Instant to) {
        return sessionMapper.findAllBySessionTimeRange(from, to);
    }

    public List<Session> getAllByInsertTimeRange(Instant from, Instant to) {
        return sessionMapper.findAllByInsertTimeRange(from, to);
    }

    @Cacheable(cacheNames = CacheNames.SESSIONS)
    public List<Session> getAllByTimeRangeAndDriverId(Instant from, Instant to, String driverId) {
        return sessionMapper.findAllByTimeRangeAndDriverId(from, to, driverId);
    }

    @Cacheable(cacheNames = CacheNames.SESSION, key = "#fileChecksum", unless = "#result == null")
    public Session getByFileChecksum(String fileChecksum) {
        return sessionMapper.findByFileChecksum(fileChecksum);
    }

    public List<Session> getAllByDriverId(String driverId) {
        return sessionMapper.findAllByDriverId(driverId);
    }

    @Transactional
    @CacheEvict(cacheNames = {CacheNames.SESSIONS, CacheNames.RANKINGS}, allEntries = true)
    public void handleSession(AccSession accSession, String fileContent, FileMetadata fileMetadata) {
        String fileName = fileMetadata.getFile().toString();

        // Ignore session without any laps
        if (accSession.getLaps().isEmpty()) {
            log.info(String.format("Ignoring session file %s because it has no laps", fileName));
            return;
        }

        // Ignore session based on a specific pattern in the server name
        if (accSession.getServerName() != null && ignorePattern != null) {
            if (ignorePattern.matcher(accSession.getServerName()).find()) {
                log.info(String.format("Ignoring session file %s because server name '%s' matches ignore pattern '%s'", fileName, accSession.getServerName(), ignorePattern));
                return;
            }
        }

        Session session = sessionConverter.convertToSession(accSession, fileContent, fileMetadata);
        Session existingSession = self.getByFileChecksum(session.getFileChecksum());
        if (existingSession != null) {
            log.info(String.format("Ignoring session file %s because it already exists", fileName));
            return;
        }

        // Insert session first to get the id (auto increment)
        sessionMapper.insert(session);

        // Actual processing of the session results
        leaderboardService.processLeaderboardLines(session, accSession, fileMetadata);
        lapService.processLaps(session, accSession, fileMetadata);
        penaltyService.processPenalties(session, accSession);

        log.info(String.format("Successfully processed session file %s", fileName));
    }
}
