package de.sustineo.acc.leaderboard.views.renderers;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import de.sustineo.acc.leaderboard.entities.ranking.DriverRanking;
import de.sustineo.acc.leaderboard.utils.FormatUtils;

import java.util.Optional;

public class DriverRankingRenderer {
    private static final String LAP_TIME_TEMPLATE = """
                <vaadin-vertical-layout>
                    <span>
                        ${item.time}
                     </span>
                    <span style="font-size: var(--lumo-font-size-s); color: var(${item.color});">
                        ${item.gap}
                    </span>
                </vaadin-vertical-layout>
            """;
    private static final String LAP_TIME_TEMPLATE_TIME = "time";
    private static final String LAP_TIME_TEMPLATE_GAP = "gap";
    private static final String LAP_TIME_TEMPLATE_COLOR = "color";

    public static Renderer<DriverRanking> createLapTimeRenderer(DriverRanking topDriverRanking) {
        Long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getLapTimeMillis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(LAP_TIME_TEMPLATE)
                .withProperty(LAP_TIME_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getLapTimeMillis()))
                .withProperty(LAP_TIME_TEMPLATE_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getLapTimeMillis() - fastestTimeMillis))
                .withProperty(LAP_TIME_TEMPLATE_COLOR, driverRanking -> getTimeColor(driverRanking.getLapTimeMillis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createSplit1Renderer(DriverRanking topDriverRanking) {
        Long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getSplit1Millis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(LAP_TIME_TEMPLATE)
                .withProperty(LAP_TIME_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSplit1Millis()))
                .withProperty(LAP_TIME_TEMPLATE_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSplit1Millis() - fastestTimeMillis))
                .withProperty(LAP_TIME_TEMPLATE_COLOR, driverRanking -> getTimeColor(driverRanking.getSplit1Millis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createSplit2Renderer(DriverRanking topDriverRanking) {
        Long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getSplit2Millis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(LAP_TIME_TEMPLATE)
                .withProperty(LAP_TIME_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSplit2Millis()))
                .withProperty(LAP_TIME_TEMPLATE_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSplit2Millis() - fastestTimeMillis))
                .withProperty(LAP_TIME_TEMPLATE_COLOR, driverRanking -> getTimeColor(driverRanking.getSplit2Millis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createSplit3Renderer(DriverRanking topDriverRanking) {
        Long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getSplit3Millis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(LAP_TIME_TEMPLATE)
                .withProperty(LAP_TIME_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSplit3Millis()))
                .withProperty(LAP_TIME_TEMPLATE_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSplit3Millis() - fastestTimeMillis))
                .withProperty(LAP_TIME_TEMPLATE_COLOR, driverRanking -> getTimeColor(driverRanking.getSplit3Millis() - fastestTimeMillis));
    }

    private static String getTimeColor(Long gapMillis) {
        if (gapMillis < 0) {
            return "--lumo-success-text-color";
        } else if (gapMillis == 0) {
            return "--lumo-secondary-text-color";
        } else {
            return "--lumo-error-text-color";
        }
    }
}
