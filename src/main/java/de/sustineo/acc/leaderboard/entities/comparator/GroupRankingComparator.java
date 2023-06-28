package de.sustineo.acc.leaderboard.entities.comparator;

import de.sustineo.acc.leaderboard.entities.ranking.GroupRanking;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class GroupRankingComparator implements Comparator<GroupRanking> {
    @Override
    public int compare(GroupRanking r1, GroupRanking r2) {
        return new CompareToBuilder()
                .append(r1.getCarGroup(), r2.getCarGroup())
                .append(r1.getTrackId(), r2.getTrackId())
                .build();
    }
}
