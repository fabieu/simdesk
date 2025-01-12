package de.sustineo.simdesk.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.simdesk.entities.LeaderboardLine;

public class LeaderboardLinePartNameGenerator implements SerializableFunction<LeaderboardLine, String> {
    @Override
    public String apply(LeaderboardLine leaderboardLine) {
        if (leaderboardLine.getBestLapTimeMillis() <= 0 || leaderboardLine.getLapCount() <= 0) {
            return "ranking-invalid";
        } else {
            return null;
        }
    }
}
