package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.CacheNames;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.LeaderboardLine;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.mybatis.mapper.LeaderboardMapper;
import de.sustineo.simdesk.services.converter.LeaderboardConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(SpringProfile.LEADERBOARD)
@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final LeaderboardConverter leaderboardConverter;
    private final LeaderboardMapper leaderboardMapper;
    private final DriverService driverService;

    @Transactional
    @CacheEvict(cacheNames = CacheNames.LEADERBOARD_LINES, key = "#session.id")
    public void processLeaderboardLines(Session session, AccSession accSession, FileMetadata fileMetadata) {
        List<LeaderboardLine> leaderboardLines = leaderboardConverter.convertToLeaderboardLines(session, accSession, fileMetadata);

        for (LeaderboardLine leaderboardLine : leaderboardLines) {
            for (Driver driver : leaderboardLine.getDrivers()) {
                driverService.upsertDriver(driver);
                leaderboardMapper.insertLeaderboardDriver(session.getId(), leaderboardLine.getCarId(), driver.getId(), driver.getDriveTimeMillis());
            }

            leaderboardMapper.insertLeaderboardLine(leaderboardLine);
        }
    }

    @Cacheable(cacheNames = CacheNames.LEADERBOARD_LINES, key = "#sessionId")
    public List<LeaderboardLine> getLeaderboardLinesBySessionId(Integer sessionId) {
        return leaderboardMapper.findBySessionIdOrderByRanking(sessionId);
    }
}
