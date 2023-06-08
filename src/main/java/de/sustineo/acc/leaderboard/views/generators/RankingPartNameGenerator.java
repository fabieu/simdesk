package de.sustineo.acc.leaderboard.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.acc.leaderboard.entities.Ranking;

public class RankingPartNameGenerator implements SerializableFunction<Ranking, String>{
    @Override
    public String apply(Ranking ranking) {
        if (ranking == null || ranking.getCarGroup() == null) {
            return null;
        }

        return "ranking-color-" + ranking.getCarGroup().name().toLowerCase();
    }
}
