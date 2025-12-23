package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.DriverAlias;
import de.sustineo.simdesk.mybatis.mapper.DriverMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Profile(SpringProfile.LEADERBOARD)
@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverMapper driverMapper;
    private final DriverAliasService driverAliasService;

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
        // Check if driver exists and if the name has changed
        Driver existingDriver = driverMapper.findById(driver.getId());
        
        if (existingDriver != null) {
            // Check if first name or last name has changed
            boolean firstNameChanged = !Objects.equals(existingDriver.getFirstName(), driver.getFirstName());
            boolean lastNameChanged = !Objects.equals(existingDriver.getLastName(), driver.getLastName());
            
            if (firstNameChanged || lastNameChanged) {
                // Store the old name as an alias
                DriverAlias alias = DriverAlias.builder()
                        .driverId(existingDriver.getId())
                        .firstName(existingDriver.getFirstName())
                        .lastName(existingDriver.getLastName())
                        .createdAt(Instant.now())
                        .build();
                driverAliasService.insertAlias(alias);
            }
        }
        
        driverMapper.upsert(driver);
    }

    public void updateDriverVisibility(Driver driver) {
        driverMapper.updateVisibility(driver);
    }
}

