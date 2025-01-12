package de.sustineo.simdesk.services.leaderboard;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Penalty;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.repositories.PenaltyRepository;
import de.sustineo.simdesk.services.converter.PenaltyConverter;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Log
@Service
@Transactional
public class PenaltyService {
    private final PenaltyConverter penaltyConverter;
    private final PenaltyRepository penaltyRepository;

    public PenaltyService(PenaltyConverter penaltyConverter,
                          PenaltyRepository penaltyRepository) {
        this.penaltyConverter = penaltyConverter;
        this.penaltyRepository = penaltyRepository;
    }

    public void processPenalties(Session session, AccSession accSession) {
        List<Penalty> penalties = penaltyConverter.convertToPenalty(session, accSession);
        penaltyRepository.saveAll(penalties);
    }

    public List<Penalty> findBySessionAndCarId(Long sessionId, Integer carId) {
        return penaltyRepository.findBySessionIdAndCarIdOrderByIdDesc(sessionId, carId);
    }
}
