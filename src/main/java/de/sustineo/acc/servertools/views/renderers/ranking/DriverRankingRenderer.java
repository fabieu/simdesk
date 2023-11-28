package de.sustineo.acc.servertools.views.renderers.ranking;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import de.sustineo.acc.servertools.entities.ranking.DriverRanking;
import de.sustineo.acc.servertools.utils.FormatUtils;

import java.util.Optional;

public class DriverRankingRenderer extends GridRenderer {
    public static Renderer<DriverRanking> createLapTimeRenderer(DriverRanking topDriverRanking) {
        long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getLapTimeMillis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getLapTimeMillis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getLapTimeMillis() - fastestTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getTimeColor(driverRanking.getLapTimeMillis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createSplit1Renderer(DriverRanking topDriverRanking) {
        long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getSplit1Millis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSplit1Millis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSplit1Millis() - fastestTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getTimeColor(driverRanking.getSplit1Millis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createSplit2Renderer(DriverRanking topDriverRanking) {
        long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getSplit2Millis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSplit2Millis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSplit2Millis() - fastestTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getTimeColor(driverRanking.getSplit2Millis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createSplit3Renderer(DriverRanking topDriverRanking) {
        long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getSplit3Millis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSplit3Millis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSplit3Millis() - fastestTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getTimeColor(driverRanking.getSplit3Millis() - fastestTimeMillis));
    }
}
