package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.Lap;
import de.sustineo.acc.leaderboard.entities.LapCount;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import de.sustineo.acc.leaderboard.entities.mapper.LapMapper;
import de.sustineo.acc.leaderboard.services.converter.LapConverter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void handleLaps(Integer sessionId, AccSession accSession) {
        List<Lap> laps = lapConverter.convertToLaps(sessionId, accSession);

        for (Lap lap : laps) {
            driverService.upsertDriver(lap.getDriver());
            insertLap(lap);
        }
    }

    public void insertLap(Lap lap) {
        lapMapper.insert(lap);
    }

    public List<LapCount> findLapCountsByPlayerId(String playerId) {
        return lapMapper.findLapCounts(playerId);
    }
}
