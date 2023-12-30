package de.sustineo.acc.servertools.views.renderers;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import de.sustineo.acc.servertools.entities.ranking.SessionRanking;
import de.sustineo.acc.servertools.utils.FormatUtils;

import java.util.Optional;

public class SessionRankingRenderer extends GridRenderer {
    public static Renderer<SessionRanking> createRaceNumberRenderer() {
        return LitRenderer.<SessionRanking>of(RACE_NUMBER_TEMPLATE)
                .withProperty(RACE_NUMBER_TEMPLATE_NUMBER, SessionRanking::getRaceNumber)
                .withProperty(RACE_NUMBER_TEMPLATE_BALLAST, SessionRanking::getBallastKg);
    }

    public static Renderer<SessionRanking> createDriversRenderer() {
        return LitRenderer.<SessionRanking>of(DRIVERS_TEMPLATE)
                .withProperty(DRIVERS_TEMPLATE_DRIVERS, SessionRanking::getDrivers);
    }

    public static Renderer<SessionRanking> createLapTimeRenderer(SessionRanking bestLapSessionRanking) {
        long bestLapTimeMillis = Optional.ofNullable(bestLapSessionRanking)
                .map(SessionRanking::getBestLapTimeMillis)
                .orElse(0L);

        return LitRenderer.<SessionRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, sessionRanking -> FormatUtils.formatLapTime(sessionRanking.getBestLapTimeMillis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, sessionRanking -> FormatUtils.formatLapTime(sessionRanking.getBestLapTimeMillis() - bestLapTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, sessionRanking -> getTimeColor(sessionRanking.getBestLapTimeMillis() - bestLapTimeMillis))
                .withProperty(TIMING_TEMPLATE_FASTEST_LAP, sessionRanking -> sessionRanking.getBestLapTimeMillis() == bestLapTimeMillis);
    }

    public static Renderer<SessionRanking> createTotalTimeRenderer(SessionRanking bestTotalTimeSessionRanking) {
        long bestTotalTimeMillis = Optional.ofNullable(bestTotalTimeSessionRanking)
                .map(SessionRanking::getTotalTimeMillis)
                .orElse(0L);

        int bestLapCount = Optional.ofNullable(bestTotalTimeSessionRanking)
                .map(SessionRanking::getLapCount)
                .orElse(0);

        return LitRenderer.<SessionRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, sessionRanking -> FormatUtils.formatTotalTime(sessionRanking.getTotalTimeMillis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, sessionRanking -> FormatUtils.formatTotalTime(sessionRanking.getTotalTimeMillis() - bestTotalTimeMillis))
                .withProperty(TIMING_TEMPLATE_LAP_COUNT, SessionRanking::getLapCount)
                .withProperty(TIMING_TEMPLATE_BEST_LAP_COUNT, sessionRanking -> bestLapCount)
                .withProperty(TIMING_TEMPLATE_COLOR, sessionRanking -> "--lumo-secondary-text-color");
    }
}
