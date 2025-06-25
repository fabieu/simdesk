package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.mapper.LapMapper;
import de.sustineo.simdesk.services.converter.LapConverter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Log
@Service
public class LapService {
    private final DriverService driverService;
    private final LapConverter lapConverter;
    private final LapMapper lapMapper;

    @Autowired
    public LapService(DriverService driverService, LapMapper lapMapper, LapConverter lapConverter) {
        this.driverService = driverService;
        this.lapMapper = lapMapper;
        this.lapConverter = lapConverter;
    }

    @Transactional
    public void processLaps(Session session, AccSession accSession, FileMetadata fileMetadata) {
        List<Lap> laps = lapConverter.convertToLaps(session, accSession, fileMetadata);
        laps.forEach(this::insertLap);
    }

    @Transactional
    public void insertLap(Lap lap) {
        driverService.upsertDriver(lap.getDriver());
        lapMapper.insert(lap);
    }

    public List<Lap> getBySessionIdAndDriverIds(Integer sessionId, List<String> driverIds) {
        return lapMapper.findBySessionIdAndDriverIds(sessionId, driverIds);
    }

    public List<Lap> getBySessionIdAndDriverId(Integer sessionId, String driverId) {
        return getBySessionIdAndDriverIds(sessionId, List.of(driverId));
    }

    public List<Lap> getBySessionId(Integer sessionId) {
        return lapMapper.findBySessionId(sessionId);
    }

    public List<Lap> getByDriverId(String driverId) {
        return lapMapper.findByDriverId(driverId);
    }
}
