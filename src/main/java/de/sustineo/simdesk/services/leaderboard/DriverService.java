package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.mapper.DriverMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class DriverService {
    private final DriverMapper driverMapper;

    public DriverService(DriverMapper driverMapper) {
        this.driverMapper = driverMapper;
    }

    public void upsertDriver(Driver driver) {
        driverMapper.upsert(driver);
    }

    public List<Driver> getAllDrivers() {
        return driverMapper.findAll();
    }

    public void updateDriverVisibility(Driver driver) {
        driverMapper.updateVisibility(driver);
    }
}

