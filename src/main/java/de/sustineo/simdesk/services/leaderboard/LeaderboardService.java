package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.LeaderboardLine;
import de.sustineo.simdesk.entities.json.kunos.AccSession;
import de.sustineo.simdesk.entities.mapper.LeaderboardMapper;
import de.sustineo.simdesk.services.converter.LeaderboardConverter;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    public void handleLeaderboard(Integer sessionId, AccSession accSession, FileMetadata fileMetadata) {
        List<LeaderboardLine> leaderboardLines = leaderboardConverter.convertToLeaderboardLines(sessionId, accSession, fileMetadata);
        leaderboardLines.forEach(leaderboardLine -> insertLeaderboardLineAsync(sessionId, leaderboardLine));
    }

    @Async
    protected void insertLeaderboardLineAsync(Integer sessionId, LeaderboardLine leaderboardLine) {
        for (Driver driver : leaderboardLine.getDrivers()) {
            driverService.upsertDriver(driver);
            leaderboardMapper.insertLeaderboardDriver(sessionId, leaderboardLine.getCarId(), driver.getPlayerId(), driver.getDriveTimeMillis());
        }

        leaderboardMapper.insertLeaderboardLine(leaderboardLine);
    }
}
