package de.sustineo.acc.leaderboard.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.acc.leaderboard.entities.Ranking;

public class CarGroupPartNameGenerator implements SerializableFunction<Ranking, String>{
    @Override
    public String apply(Ranking ranking) {
        if (ranking == null || ranking.getCarGroup() == null) {
            return null;
        }

        String rankingColorGroup = switch (ranking.getCarGroup()) {
            case GT3 -> "gt3";
            case GT4 -> "gt4";
            case CUP, ST, CHL  -> "gtc";
            case TCX -> "tcx";
        };

        return "ranking-car-group-" + rankingColorGroup;
    }
}
