package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.mybatis.mapper.DriverMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverMapper driverMapper;

    public Driver getDriverById(String driverId) {
        return driverMapper.findById(driverId);
    }

    public List<Driver> getAllDrivers() {
        return driverMapper.findAll();
    }

    public List<String> getDriverIdsBySessionIdAndCarId(Integer id, Integer carId) {
        return driverMapper.findDriverIdsBySessionIdAndCarId(id, carId);
    }

    public void upsertDriver(Driver driver) {
        driverMapper.upsert(driver);
    }

    public void updateDriverVisibility(Driver driver) {
        driverMapper.updateVisibility(driver);
    }
}

