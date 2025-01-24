package de.sustineo.simdesk.services.converter;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.json.kunos.acc.AccDriver;
import de.sustineo.simdesk.entities.json.kunos.acc.AccLeaderboardLine;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class DriverConverter extends BaseConverter {
    public Driver convertToDriver(AccDriver accDriver, FileMetadata fileMetadata) {
        return Driver.builder()
                .id(StringUtils.trim(accDriver.getPlayerId()))
                .firstName(StringUtils.trim(accDriver.getFirstName()))
                .lastName(StringUtils.trim(accDriver.getLastName()))
                .shortName(StringUtils.trim(accDriver.getShortName()))
                .lastActivity(fileMetadata.getModifiedDatetime())
                .build();
    }

    public Driver convertToLeaderboardDriver(AccDriver accDriver, FileMetadata fileMetadata, AccLeaderboardLine accLeaderboardLine) {
        Driver driver = convertToDriver(accDriver, fileMetadata);

        if (accLeaderboardLine.getDriverTotalTimes().isEmpty()) {
            return driver;
        }

        int driverIndex = getDriverIndex(accDriver, accLeaderboardLine);
        if (driverIndex != -1) {
            driver.setDriveTimeMillis(accLeaderboardLine.getDriverTotalTimes().get(driverIndex));
        }

        return driver;
    }

    private Integer getDriverIndex(AccDriver accDriver, AccLeaderboardLine accLeaderboardLine) {
        return accLeaderboardLine.getCar().getDrivers().indexOf(accDriver);
    }
}
