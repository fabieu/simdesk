package de.sustineo.acc.servertools.entities.comparator;

import de.sustineo.acc.servertools.entities.ranking.SessionRanking;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class SessionRankingLapTimeComparator implements Comparator<SessionRanking> {
    @Override
    public int compare(SessionRanking r1, SessionRanking r2) {
        return new CompareToBuilder()
                .append(r1.getBestLapTimeMillis(), r2.getBestLapTimeMillis())
                .build();
    }
}
