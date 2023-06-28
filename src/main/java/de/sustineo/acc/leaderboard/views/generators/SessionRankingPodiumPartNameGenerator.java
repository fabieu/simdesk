package de.sustineo.acc.leaderboard.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.acc.leaderboard.entities.ranking.SessionRanking;

public class SessionRankingPodiumPartNameGenerator implements SerializableFunction<SessionRanking, String> {
    @Override
    public String apply(SessionRanking sessionRanking) {
        return switch (sessionRanking.getRanking()){
            case 1 -> "ranking-podium-first";
            case 2 -> "ranking-podium-second";
            case 3 -> "ranking-podium-third";
            default -> null;
        };
    }
}
