package de.sustineo.acc.leaderboard.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.acc.leaderboard.entities.ranking.SessionRanking;

import java.util.ArrayList;
import java.util.List;

public class SessionRankingPartNameGenerator implements SerializableFunction<SessionRanking, String> {
    private final SessionRanking bestTotalTimeSessionRanking;
    private static final double DNF_THRESHOLD = 0.75;

    public SessionRankingPartNameGenerator(SessionRanking bestTotalTimeSessionRanking) {
        this.bestTotalTimeSessionRanking = bestTotalTimeSessionRanking;
    }

    @Override
    public String apply(SessionRanking sessionRanking) {
        List<String> partNames = new ArrayList<>();

        applyPodiumClass(partNames, sessionRanking);
        applyDNSOrDNFClass(partNames, sessionRanking);

        return String.join(" ", partNames);
    }

    private void applyDNSOrDNFClass(List<String> partNames, SessionRanking sessionRanking) {
        if (sessionRanking.getLapCount() <= 0) {
            partNames.add("ranking-dns");
        } else if (sessionRanking.getTotalTimeMillis() < bestTotalTimeSessionRanking.getTotalTimeMillis() * DNF_THRESHOLD) {
            partNames.add("ranking-dnf");
        }
    }

    private void applyPodiumClass(List<String> partNames, SessionRanking sessionRanking) {
        String partName = switch (sessionRanking.getRanking()) {
            case 1 -> "ranking-podium-first";
            case 2 -> "ranking-podium-second";
            case 3 -> "ranking-podium-third";
            default -> null;
        };

        if (partName != null) {
            partNames.add(partName);
        }
    }
}
