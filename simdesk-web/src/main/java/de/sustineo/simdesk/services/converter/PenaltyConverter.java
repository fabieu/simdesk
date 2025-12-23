package de.sustineo.simdesk.services.converter;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.Penalty;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.json.kunos.acc.AccPenalty;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Profile(SpringProfile.LEADERBOARD)
@Log
@Service
public class PenaltyConverter {
    public List<Penalty> convertToPenalty(Session session, AccSession accSession) {
        List<Penalty> penalties = accSession.getPenalties().stream()
                .map(accPenalty -> convertToPenalty(session, accPenalty, false))
                .toList();

        List<Penalty> postRacePenalties = accSession.getPostRacePenalties().stream()
                .map(accPenalty -> convertToPenalty(session, accPenalty, true))
                .toList();

        return Stream.concat(penalties.stream(), postRacePenalties.stream()).toList();
    }

    public Penalty convertToPenalty(Session session, AccPenalty accPenalty, boolean postRace) {
        return Penalty.builder()
                .sessionId(session.getId())
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
