package de.sustineo.simdesk.views.renderers;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import de.sustineo.simdesk.entities.ranking.DriverRanking;
import de.sustineo.simdesk.utils.FormatUtils;

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

    public static Renderer<DriverRanking> createSector1Renderer(DriverRanking topDriverRanking) {
        long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getSector1Millis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSector1Millis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSector1Millis() - fastestTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getTimeColor(driverRanking.getSector1Millis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createSector2Renderer(DriverRanking topDriverRanking) {
        long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getSector2Millis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSector2Millis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSector2Millis() - fastestTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getTimeColor(driverRanking.getSector2Millis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createSector3Renderer(DriverRanking topDriverRanking) {
        long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getSector3Millis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSector3Millis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSector3Millis() - fastestTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getTimeColor(driverRanking.getSector3Millis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createDriverRenderer() {
        return LitRenderer.<DriverRanking>of(DRIVER_REFERENCE_TEMPLATE)
                .withProperty(DRIVER_REFERENCE_TEMPLATE_DRIVER, DriverRanking::getDriver)
                .withFunction(DRIVER_REFERENCE_TEMPLATE_CLICK_HANDLER, driverRanking -> redirectToDriverProfile(driverRanking.getDriver()));
    }
}
