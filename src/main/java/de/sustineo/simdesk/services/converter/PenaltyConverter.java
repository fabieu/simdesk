package de.sustineo.simdesk.services.converter;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Penalty;
import de.sustineo.simdesk.entities.json.kunos.AccPenalty;
import de.sustineo.simdesk.entities.json.kunos.AccSession;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Log
@Service
public class PenaltyConverter {
    public List<Penalty> convertToPenalty(Integer sessionId, AccSession accSession) {
        List<Penalty> penalties = accSession.getPenalties().stream()
                .map(accPenalty -> convertToPenalty(sessionId, accPenalty, false))
                .toList();

        List<Penalty> postRacePenalties = accSession.getPostRacePenalties().stream()
                .map(accPenalty -> convertToPenalty(sessionId, accPenalty, true))
                .toList();

        return Stream.concat(penalties.stream(), postRacePenalties.stream()).toList();
    }

    public Penalty convertToPenalty(Integer sessionId, AccPenalty accPenalty, boolean postRace) {
        return Penalty.builder()
                .sessionId(sessionId)
                .carId(accPenalty.getCarId())
                .reason(accPenalty.getReason())
                .penalty(accPenalty.getPenalty())
                .penaltyValue(accPenalty.getPenaltyValue())
                .violationLap(accPenalty.getViolationInLap())
                .clearedLap(accPenalty.getClearedInLap())
                .postRace(postRace)
                .build();
    }
}