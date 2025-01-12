package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.Visibility;
import de.sustineo.simdesk.repositories.DriverRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class DriverService {
    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Transactional
    public void upsertDriver(Driver driver) {
        Driver existingDriver = driverRepository.findByPlayerId(driver.getPlayerId());
        if (existingDriver == null) {
            driverRepository.save(driver);
            return;
        }

        if (driver.getFirstName() != null) {
            existingDriver.setFirstName(driver.getFirstName());
        }

        if (driver.getLastName() != null) {
            existingDriver.setLastName(driver.getLastName());
        }

        if (driver.getShortName() != null) {
            existingDriver.setShortName(driver.getShortName());
        }

        if (driver.getVisibility() != null) {
            existingDriver.setVisibility(driver.getVisibility());
        } else if (existingDriver.getVisibility() == null) {
            existingDriver.setVisibility(Visibility.PUBLIC);
        }

        if (driver.getLastActivity() != null && (existingDriver.getLastActivity() == null || driver.getLastActivity().isAfter(existingDriver.getLastActivity()))) {
            existingDriver.setLastActivity(driver.getLastActivity());
        }

        driverRepository.save(existingDriver);
    }


    @Transactional
    public void updateDriverVisibility(Driver driver) {
        driverRepository.updateVisibility(driver);
    }
}

