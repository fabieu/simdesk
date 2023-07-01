package de.sustineo.acc.leaderboard.views.renderers.ranking;

public class RankingRenderer {
    public static final String DNS = "DNS";

    public static String getTimeColor(Long gapMillis) {
        if (gapMillis < 0) {
            return "--lumo-success-text-color";
        } else if (gapMillis == 0) {
            return "--lumo-secondary-text-color";
        } else {
            return "--lumo-error-text-color";
        }
    }
}
