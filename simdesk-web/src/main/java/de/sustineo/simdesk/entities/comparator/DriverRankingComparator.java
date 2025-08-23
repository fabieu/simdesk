package de.sustineo.simdesk.entities.comparator;

import de.sustineo.simdesk.entities.ranking.DriverRanking;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class DriverRankingComparator implements Comparator<DriverRanking> {
    @Override
    public int compare(DriverRanking r1, DriverRanking r2) {
        return new CompareToBuilder()
                .append(r1.getLapTimeMillis(), r2.getLapTimeMillis())
                .build();
    }
}
