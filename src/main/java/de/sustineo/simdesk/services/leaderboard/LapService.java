package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.LapCount;
import de.sustineo.simdesk.entities.json.kunos.AccSession;
import de.sustineo.simdesk.entities.mapper.LapMapper;
import de.sustineo.simdesk.services.converter.LapConverter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    public void handleLaps(Integer sessionId, AccSession accSession, FileMetadata fileMetadata) {
        List<Lap> laps = lapConverter.convertToLaps(sessionId, accSession, fileMetadata);
        laps.forEach(this::insertLapAsync);
    }

    @Async
    public void insertLapAsync(Lap lap) {
        driverService.upsertDriver(lap.getDriver());
        lapMapper.insert(lap);
    }

    public List<LapCount> getLapCountsByPlayerId(String playerId) {
        return lapMapper.findLapCounts(playerId);
    }

    public List<Lap> getLapsBySessionAndDrivers(int sessionId, List<String> playerIds) {
        return lapMapper.findBySessionAndDrivers(sessionId, playerIds);
    }

    public long getLapCount() {
        return lapMapper.count();
    }
}
