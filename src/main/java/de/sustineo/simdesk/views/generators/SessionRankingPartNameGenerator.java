package de.sustineo.simdesk.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.simdesk.entities.ranking.SessionRanking;

public class SessionRankingPartNameGenerator implements SerializableFunction<SessionRanking, String> {
    @Override
    public String apply(SessionRanking sessionRanking) {
        if (sessionRanking.getBestLapTimeMillis() <= 0 || sessionRanking.getLapCount() <= 0) {
            return "ranking-invalid";
        }

        return null;
    }
}
