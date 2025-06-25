package de.sustineo.simdesk.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.simdesk.entities.LeaderboardLine;

public class LeaderboardLinePodiumPartNameGenerator implements SerializableFunction<LeaderboardLine, String> {
    @Override
    public String apply(LeaderboardLine leaderboardLine) {
        return switch (leaderboardLine.getRanking()) {
            case 1 -> "ranking-podium-first";
            case 2 -> "ranking-podium-second";
            case 3 -> "ranking-podium-third";
            default -> null;
        };
    }
}
