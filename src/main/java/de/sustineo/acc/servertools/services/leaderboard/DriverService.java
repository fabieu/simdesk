package de.sustineo.acc.servertools.services.leaderboard;

import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.entities.Driver;
import de.sustineo.acc.servertools.entities.LapCount;
import de.sustineo.acc.servertools.entities.comparator.DriverComparator;
import de.sustineo.acc.servertools.entities.mapper.DriverMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class DriverService {
    private final DriverMapper driverMapper;
    private final LapService lapService;

    public DriverService(DriverMapper driverMapper, @Lazy LapService lapService) {
        this.driverMapper = driverMapper;
        this.lapService = lapService;
    }

    public void upsertDriver(Driver driver) {
        driverMapper.upsert(driver);
    }

    public List<Driver> getWithDetails() {
        List<Driver> drivers = driverMapper.findAll();

        return drivers.stream()
                .map(driver -> {
                    List<LapCount> lapCounts = lapService.findLapCountsByPlayerId(driver.getPlayerId());
                    driver.setValidLapsCount(lapCounts.stream().filter(LapCount::getValid).map(LapCount::getLapCount).findFirst().orElse(0));
                    driver.setInvalidLapsCount(lapCounts.stream().filter(lapCount -> !lapCount.getValid()).map(LapCount::getLapCount).findFirst().orElse(0));
                    return driver;
                })
                .sorted(new DriverComparator())
                .toList();
    }

    public long getDriverCount() {
        return driverMapper.count();
    }
}
