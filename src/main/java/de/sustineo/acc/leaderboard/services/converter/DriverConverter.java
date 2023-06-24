package de.sustineo.acc.leaderboard.services.converter;

import de.sustineo.acc.leaderboard.entities.Driver;
import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.json.AccDriver;
import org.springframework.stereotype.Service;

@Service
public class DriverConverter {
    public Driver convertToDriver(AccDriver accDriver, FileMetadata fileMetadata) {
        return Driver.builder()
                .playerId(accDriver.getPlayerId())
                .firstName(accDriver.getFirstName())
                .lastName(accDriver.getLastName())
                .shortName(accDriver.getShortName())
                .lastActivity(fileMetadata.getModifiedDatetime())
                .build();
    }
}
