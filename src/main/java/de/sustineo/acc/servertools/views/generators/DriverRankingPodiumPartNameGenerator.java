package de.sustineo.acc.servertools.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.acc.servertools.entities.ranking.DriverRanking;

public class DriverRankingPodiumPartNameGenerator implements SerializableFunction<DriverRanking, String> {
    @Override
    public String apply(DriverRanking driverRanking) {
        return switch (driverRanking.getRanking()){
            case 1 -> "ranking-podium-first";
            case 2 -> "ranking-podium-second";
            case 3 -> "ranking-podium-third";
            default -> null;
        };
    }
}
