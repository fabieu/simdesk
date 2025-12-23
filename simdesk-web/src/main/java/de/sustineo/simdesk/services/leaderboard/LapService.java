package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.CacheNames;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.mybatis.mapper.LapMapper;
import de.sustineo.simdesk.services.converter.LapConverter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(SpringProfile.LEADERBOARD)
@Log
@Service
public class LapService {
    private final LapService self;
    private final LapConverter lapConverter;
    private final LapMapper lapMapper;
    private final DriverService driverService;

    @Autowired
    public LapService(@Lazy LapService lapService,
                      LapMapper lapMapper,
                      LapConverter lapConverter,
                      DriverService driverService) {
        this.self = lapService;
        this.lapMapper = lapMapper;
        this.lapConverter = lapConverter;
        this.driverService = driverService;
    }

    @Transactional
    public void processLaps(Session session, AccSession accSession, FileMetadata fileMetadata) {
        List<Lap> laps = lapConverter.convertToLaps(session, accSession, fileMetadata);
        laps.forEach(self::insertLap);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.LAPS_SESSION, key = "#lap.sessionId", condition = "#lap.sessionId != null"),
            @CacheEvict(cacheNames = CacheNames.LAPS_DRIVER, key = "#lap.driver.id", condition = "#lap.driver?.id != null"),
    })
    public void insertLap(Lap lap) {
        driverService.upsertDriver(lap.getDriver());
        lapMapper.insert(lap);
    }

    @Cacheable(cacheNames = CacheNames.LAPS_SESSION, key = "#sessionId")
    public List<Lap> getBySessionId(Integer sessionId) {
        return lapMapper.findBySessionId(sessionId);
    }

    @Cacheable(cacheNames = CacheNames.LAPS_DRIVER, key = "#driverId")
    public List<Lap> getByDriverId(String driverId) {
        return lapMapper.findByDriverId(driverId);
    }

    public List<Lap> getBySessionIdAndDriverIds(Integer sessionId, List<String> driverIds) {
        return self.getBySessionId(sessionId).stream()
                .filter(lap -> lap.getDriver() != null && driverIds.contains(lap.getDriver().getId()))
                .toList();
    }
}
