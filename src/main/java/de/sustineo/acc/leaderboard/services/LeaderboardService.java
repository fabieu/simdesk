package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.Driver;
import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.LeaderboardLine;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import de.sustineo.acc.leaderboard.entities.mapper.LeaderboardMapper;
import de.sustineo.acc.leaderboard.services.converter.LeaderboardConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

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

        for (LeaderboardLine leaderboardLine : leaderboardLines) {
            driverService.upsertDrivers(leaderboardLine.getDrivers());
            insertLeaderboardDrivers(sessionId, leaderboardLine.getDrivers(), leaderboardLine.getCarId());
            insertLeaderboardLine(leaderboardLine);
        }
    }

    @Async
    protected void insertLeaderboardDrivers(Integer sessionId, List<Driver> drivers, Integer carId) {
        drivers.forEach(driver -> leaderboardMapper.insertLeaderboardDriver(sessionId, carId, driver.getPlayerId()));
    }

    protected void insertLeaderboardLine(LeaderboardLine leaderboardLine) {
        leaderboardMapper.insertLeaderboardLine(leaderboardLine);
    }
}
