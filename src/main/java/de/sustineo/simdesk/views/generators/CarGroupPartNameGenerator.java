package de.sustineo.simdesk.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.simdesk.entities.ranking.GroupRanking;

public class CarGroupPartNameGenerator implements SerializableFunction<GroupRanking, String>{
    @Override
    public String apply(GroupRanking groupRanking) {
        if (groupRanking == null || groupRanking.getCarGroup() == null) {
            return null;
        }

        String rankingColorGroup = groupRanking.getCarGroup().name().toLowerCase();

        return "ranking-car-group-" + rankingColorGroup;
    }
}
