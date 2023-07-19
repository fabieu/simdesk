package de.sustineo.acc.leaderboard.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.acc.leaderboard.entities.enums.SessionType;
import de.sustineo.acc.leaderboard.entities.ranking.SessionRanking;

public class SessionRankingDNFNameGenerator implements SerializableFunction<SessionRanking, String> {
    private final SessionRanking bestTotalTimeSessionRanking;
    private static final double DNF_THRESHOLD = 0.75;

    public SessionRankingDNFNameGenerator(SessionRanking bestTotalTimeSessionRanking) {
        this.bestTotalTimeSessionRanking = bestTotalTimeSessionRanking;
    }

    @Override
    public String apply(SessionRanking sessionRanking) {
        // Only apply for race sessions
        if (sessionRanking.getSession().getSessionType() != SessionType.R){
            return null;
        }

        if (sessionRanking.getTotalTimeMillis() < bestTotalTimeSessionRanking.getTotalTimeMillis() * DNF_THRESHOLD) {
            return "ranking-dnf";
        }

        return null;
    }
}
