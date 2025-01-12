package de.sustineo.simdesk.views.renderers;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import de.sustineo.simdesk.entities.LeaderboardLine;
import de.sustineo.simdesk.utils.FormatUtils;

import java.util.Optional;

public class LeaderboardLineRenderer extends GridRenderer {
    public static Renderer<LeaderboardLine> createRaceNumberRenderer() {
        return LitRenderer.<LeaderboardLine>of(RACE_NUMBER_TEMPLATE)
                .withProperty(RACE_NUMBER_TEMPLATE_NUMBER, LeaderboardLine::getRaceNumber)
                .withProperty(RACE_NUMBER_TEMPLATE_BALLAST, LeaderboardLine::getBallastKg);
    }

    public static Renderer<LeaderboardLine> createDriversRenderer() {
        return LitRenderer.<LeaderboardLine>of(DRIVERS_TEMPLATE)
                .withProperty(DRIVERS_TEMPLATE_DRIVERS, LeaderboardLine::getDrivers);
    }

    public static Renderer<LeaderboardLine> createLapTimeRenderer(LeaderboardLine bestLap) {
        long bestLapTimeMillis = Optional.ofNullable(bestLap)
                .map(LeaderboardLine::getBestLapTimeMillis)
                .orElse(0L);

        return LitRenderer.<LeaderboardLine>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, leaderboardLine -> FormatUtils.formatLapTime(leaderboardLine.getBestLapTimeMillis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, leaderboardLine -> {
                    if (leaderboardLine.getBestLapTimeMillis() > 0) {
                        return FormatUtils.formatLapTime(leaderboardLine.getBestLapTimeMillis() - bestLapTimeMillis);
                    } else {
                        return FormatUtils.formatLapTime(0L);
                    }
                })
                .withProperty(TIMING_TEMPLATE_COLOR, leaderboardLine -> {
                    if (leaderboardLine.getBestLapTimeMillis() > 0) {
                        return getTimeColor(leaderboardLine.getBestLapTimeMillis() - bestLapTimeMillis);
                    } else {
                        return getTimeColor(0L);
                    }
                })
                .withProperty(TIMING_TEMPLATE_FASTEST_LAP, leaderboardLine -> leaderboardLine.getBestLapTimeMillis() != 0 && leaderboardLine.getBestLapTimeMillis() == bestLapTimeMillis);
    }

    public static Renderer<LeaderboardLine> createTotalTimeRenderer(LeaderboardLine bestTotalTime) {
        long bestTotalTimeMillis = Optional.ofNullable(bestTotalTime)
                .map(LeaderboardLine::getTotalTimeMillis)
                .orElse(0L);

        int bestLapCount = Optional.ofNullable(bestTotalTime)
                .map(LeaderboardLine::getLapCount)
                .orElse(0);

        return LitRenderer.<LeaderboardLine>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, leaderboardLine -> FormatUtils.formatTotalTime(leaderboardLine.getTotalTimeMillis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, leaderboardLine -> FormatUtils.formatTotalTime(leaderboardLine.getTotalTimeMillis() - bestTotalTimeMillis))
                .withProperty(TIMING_TEMPLATE_LAP_COUNT, LeaderboardLine::getLapCount)
                .withProperty(TIMING_TEMPLATE_BEST_LAP_COUNT, leaderboardLine -> bestLapCount)
                .withProperty(TIMING_TEMPLATE_COLOR, leaderboardLine -> "--lumo-secondary-text-color");
    }
}
