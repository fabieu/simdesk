package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.Lap;
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
    private final LapMapper lapMapper;
    private final LapConverter lapConverter;

    @Autowired
    public LapService(LapMapper lapMapper, LapConverter lapConverter) {
        this.lapMapper = lapMapper;
        this.lapConverter = lapConverter;
    }

    public void handleLaps(Integer sessionId, AccSession accSession) {
        List<Lap> laps = lapConverter.convertToLaps(sessionId, accSession);
        laps.forEach(this::insertLap);
    }

    public void insertLap(Lap lap) {
        lapMapper.insert(lap);
    }
}
