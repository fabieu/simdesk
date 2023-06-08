package de.sustineo.acc.leaderboard.entities.comparator;

import de.sustineo.acc.leaderboard.entities.Ranking;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class RankingComparator implements Comparator<Ranking> {
    @Override
    public int compare(Ranking r1, Ranking r2) {
        return new CompareToBuilder()
                .append(r1.getCarGroup(), r2.getCarGroup())
                .append(r1.getTrackId(), r2.getTrackId())
                .build();
    }
}
