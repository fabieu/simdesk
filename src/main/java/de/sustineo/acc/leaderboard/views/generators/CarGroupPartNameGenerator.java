package de.sustineo.acc.leaderboard.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.acc.leaderboard.entities.ranking.GroupRanking;

public class CarGroupPartNameGenerator implements SerializableFunction<GroupRanking, String>{
    @Override
    public String apply(GroupRanking groupRanking) {
        if (groupRanking == null || groupRanking.getCarGroup() == null) {
            return null;
        }

        String rankingColorGroup = switch (groupRanking.getCarGroup()) {
            case GT3 -> "gt3";
            case GT4 -> "gt4";
            case CUP, ST, CHL  -> "gtc";
            case TCX -> "tcx";
        };

        return "ranking-car-group-" + rankingColorGroup;
    }
}
