package de.sustineo.simdesk.services.converter;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.*;
import de.sustineo.simdesk.entities.json.kunos.acc.AccDriver;
import de.sustineo.simdesk.entities.json.kunos.acc.AccLeaderboardLine;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class LeaderboardConverter extends BaseConverter {
    private final DriverConverter driverConverter;

    public LeaderboardConverter(DriverConverter driverConverter) {
        this.driverConverter = driverConverter;
    }

    public LeaderboardLine convertToLeaderboardLine(Integer index, Session session, AccLeaderboardLine accLeaderboardLine) {
        return LeaderboardLine.builder()
                .session(session)
                .ranking(index + 1)
                .cupCategory(accLeaderboardLine.getCar().getCupCategory())
                .carId(accLeaderboardLine.getCar().getCarId())
                .carModelId(accLeaderboardLine.getCar().getCarModel())
                .ballastKg(accLeaderboardLine.getCar().getBallastKg())
                .raceNumber(accLeaderboardLine.getCar().getRaceNumber())
                .bestLapTimeMillis(fixBadTiming(accLeaderboardLine.getTiming().getBestLap()))
                .bestSplit1Millis(fixBadTiming(accLeaderboardLine.getTiming().getBestSplits().get(0)))
                .bestSplit2Millis(fixBadTiming(accLeaderboardLine.getTiming().getBestSplits().get(1)))
                .bestSplit3Millis(fixBadTiming(accLeaderboardLine.getTiming().getBestSplits().get(2)))
                .totalTimeMillis(fixBadTiming(accLeaderboardLine.getTiming().getTotalTime()))
                .lapCount(accLeaderboardLine.getTiming().getLapCount())
                .build();
    }

    public List<LeaderboardDriver> convertToLeaderboardDrivers(Session session, AccLeaderboardLine accLeaderboardLine, FileMetadata fileMetadata) {
        List<LeaderboardDriver> leaderboardDrivers = new ArrayList<>();

        for (AccDriver accDriver : accLeaderboardLine.getCar().getDrivers()) {
            Driver driver = driverConverter.convertToDriver(accDriver, fileMetadata, accLeaderboardLine);
            LeaderboardDriver leaderboardDriver = convertToLeaderboardDriver(session, accLeaderboardLine.getCar().getCarId(), driver);
            leaderboardDrivers.add(leaderboardDriver);
        }

        return leaderboardDrivers;
    }

    private LeaderboardDriver convertToLeaderboardDriver(Session session, Integer carId, Driver driver) {
        return LeaderboardDriver.builder()
                .driver(driver)
                .session(session)
                .carId(carId)
                .driveTimeMillis(driver.getDriveTimeMillis())
                .build();
    }
}
