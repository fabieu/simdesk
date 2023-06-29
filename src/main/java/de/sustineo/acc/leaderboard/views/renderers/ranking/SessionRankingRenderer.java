package de.sustineo.acc.leaderboard.views.renderers.ranking;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import de.sustineo.acc.leaderboard.entities.ranking.SessionRanking;
import de.sustineo.acc.leaderboard.utils.FormatUtils;

import java.util.Optional;

import static de.sustineo.acc.leaderboard.views.renderers.ranking.RankingTemplates.*;

public class SessionRankingRenderer {
    public static Renderer<SessionRanking> createRaceNumberRenderer() {
        return LitRenderer.<SessionRanking>of(RACE_NUMBER_TEMPLATE)
                .withProperty(RACE_NUMBER_TEMPLATE_NUMBER, SessionRanking::getRaceNumber);
    }

    public static Renderer<SessionRanking> createDriversRenderer() {
        return LitRenderer.<SessionRanking>of(DRIVERS_TEMPLATE)
                .withProperty(DRIVERS_TEMPLATE_DRIVERS, SessionRanking::getDrivers);
    }

    public static Renderer<SessionRanking> createLapTimeRenderer(SessionRanking bestLapSessionRanking) {
        Long bestLapTimeMillis = Optional.ofNullable(bestLapSessionRanking)
                .map(SessionRanking::getBestLapTimeMillis)
                .orElse(0L);

        return LitRenderer.<SessionRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, sessionRanking -> FormatUtils.formatLapTime(sessionRanking.getBestLapTimeMillis()))
                .withProperty(TIMING_TEMPLATE_GAP, sessionRanking -> FormatUtils.formatLapTime(sessionRanking.getBestLapTimeMillis() - bestLapTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, sessionRanking -> RankingRenderer.getTimeColor(sessionRanking.getBestLapTimeMillis() - bestLapTimeMillis));
    }

    public static Renderer<SessionRanking> createTotalTimeRenderer(SessionRanking bestTotalTimeSessionRanking) {
        Long bestTotalTimeMillis = Optional.ofNullable(bestTotalTimeSessionRanking)
                .map(SessionRanking::getTotalTimeMillis)
                .orElse(0L);

        return LitRenderer.<SessionRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, sessionRanking -> FormatUtils.formatTotalTime(sessionRanking.getTotalTimeMillis()))
                .withProperty(TIMING_TEMPLATE_GAP, sessionRanking -> FormatUtils.formatTotalTime(sessionRanking.getTotalTimeMillis() - bestTotalTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, sessionRanking -> RankingRenderer.getTimeColor(sessionRanking.getTotalTimeMillis() - bestTotalTimeMillis));
    }
}
