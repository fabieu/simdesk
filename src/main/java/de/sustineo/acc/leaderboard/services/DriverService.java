package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.Driver;
import de.sustineo.acc.leaderboard.entities.mapper.DriverMapper;
import org.springframework.stereotype.Service;

@Service
public class DriverService {
    private final DriverMapper driverMapper;

    public DriverService(DriverMapper driverMapper) {
        this.driverMapper = driverMapper;
    }

    public void upsertDriver(Driver driver) {
        driverMapper.upsert(driver);
    }

    public Driver findByPlayerId(String playerId) {
        return driverMapper.findByPlayerId(playerId);
    }

    public String getDriverNameByPlayerId(String playerId) {
        Driver driver = findByPlayerId(playerId);

        if (driver == null || driver.getFirstName() == null || driver.getLastName() == null) {
            return null;
        }

        String driverFullName = String.join(" ", driver.getFirstName(), driver.getLastName());

        if (driver.getShortName() == null) {
            return driverFullName;
        } else {
            return driverFullName + " (" + driver.getShortName() + ")";
        }
    }
}
