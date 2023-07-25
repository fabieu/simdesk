package de.sustineo.acc.servertools.services.converter;

import de.sustineo.acc.servertools.entities.Driver;
import de.sustineo.acc.servertools.entities.FileMetadata;
import de.sustineo.acc.servertools.entities.json.AccDriver;
import de.sustineo.acc.servertools.entities.json.AccLeaderboardLine;
import org.springframework.stereotype.Service;

@Service
public class DriverConverter extends BaseConverter {
    public Driver convertToDriver(AccDriver accDriver, FileMetadata fileMetadata) {
        return Driver.builder()
                .playerId(accDriver.getPlayerId())
                .firstName(accDriver.getFirstName())
                .lastName(accDriver.getLastName())
                .shortName(accDriver.getShortName())
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
