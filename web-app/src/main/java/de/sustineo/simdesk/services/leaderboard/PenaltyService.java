package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Penalty;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.mybatis.mapper.PenaltyMapper;
import de.sustineo.simdesk.services.converter.PenaltyConverter;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void processPenalties(Session session, AccSession accSession) {
        List<Penalty> penalties = penaltyConverter.convertToPenalty(session, accSession);
        penalties.forEach(this::insertPenalty);
    }

    @Transactional
    protected void insertPenalty(Penalty penalty) {
        penaltyMapper.insert(penalty);
    }

    public List<Penalty> findBySessionIdAndCarId(int sessionId, int carId) {
        return penaltyMapper.findBySessionIdAndCarId(sessionId, carId);
    }
}
