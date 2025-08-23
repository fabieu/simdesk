package de.sustineo.simdesk.entities.comparator;

import de.sustineo.simdesk.entities.LeaderboardLine;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class LeaderboardLineLapTimeComparator implements Comparator<LeaderboardLine> {
    @Override
    public int compare(LeaderboardLine r1, LeaderboardLine r2) {
        return new CompareToBuilder()
                .append(r1.getBestLapTimeMillis(), r2.getBestLapTimeMillis())
                .build();
    }
}
