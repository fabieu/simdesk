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
}
