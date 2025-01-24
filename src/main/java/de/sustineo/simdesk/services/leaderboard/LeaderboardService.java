package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.LeaderboardLine;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.mapper.LeaderboardMapper;
import de.sustineo.simdesk.services.converter.LeaderboardConverter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class LeaderboardService {
    private final LeaderboardConverter leaderboardConverter;
    private final LeaderboardMapper leaderboardMapper;
    private final DriverService driverService;

    public LeaderboardService(LeaderboardConverter leaderboardConverter, DriverService driverService, LeaderboardMapper leaderboardMapper) {
        this.leaderboardConverter = leaderboardConverter;
        this.driverService = driverService;
        this.leaderboardMapper = leaderboardMapper;
    }

    @Transactional
    public void processLeaderboardLines(Session session, AccSession accSession, FileMetadata fileMetadata) {
        List<LeaderboardLine> leaderboardLines = leaderboardConverter.convertToLeaderboardLines(session, accSession, fileMetadata);
        leaderboardLines.forEach(leaderboardLine -> insertLeaderboardLine(session.getId(), leaderboardLine));
    }

    @Transactional
    protected void insertLeaderboardLine(Integer sessionId, LeaderboardLine leaderboardLine) {
        for (Driver driver : leaderboardLine.getDrivers()) {
            driverService.upsertDriver(driver);
            leaderboardMapper.insertLeaderboardDriver(sessionId, leaderboardLine.getCarId(), driver.getId(), driver.getDriveTimeMillis());
        }

        leaderboardMapper.insertLeaderboardLine(leaderboardLine);
    }

    public List<LeaderboardLine> getLeaderboardLinesBySessionId(Integer sessionId) {
        return leaderboardMapper.findBySessionIdOrderByRanking(sessionId);
    }
}
