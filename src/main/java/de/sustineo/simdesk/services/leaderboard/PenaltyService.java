package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Penalty;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.entities.mapper.PenaltyMapper;
import de.sustineo.simdesk.services.converter.PenaltyConverter;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Log
@Service
public class PenaltyService {
    private final PenaltyConverter penaltyConverter;
    private final PenaltyMapper penaltyMapper;

    public PenaltyService(PenaltyConverter penaltyConverter, PenaltyMapper penaltyMapper) {
        this.penaltyConverter = penaltyConverter;
        this.penaltyMapper = penaltyMapper;
    }

    public void handlePenalties(Long sessionId, AccSession accSession) {
        List<Penalty> penalties = penaltyConverter.convertToPenalty(sessionId, accSession);
        penalties.forEach(this::insertPenaltyAsync);
    }

    public List<Penalty> findBySessionAndCarId(Long sessionId, int carId) {
        return penaltyMapper.findBySessionAndCarId(sessionId, carId);
    }

    @Async
    protected void insertPenaltyAsync(Penalty penalty) {
        penaltyMapper.insert(penalty);
    }
}
