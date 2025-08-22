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
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getLapTimeColor(driverRanking.getLapTimeMillis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createSector1Renderer(DriverRanking topDriverRanking) {
        long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getSector1Millis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSector1Millis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSector1Millis() - fastestTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getLapTimeColor(driverRanking.getSector1Millis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createSector2Renderer(DriverRanking topDriverRanking) {
        long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getSector2Millis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSector2Millis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSector2Millis() - fastestTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getLapTimeColor(driverRanking.getSector2Millis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createSector3Renderer(DriverRanking topDriverRanking) {
        long fastestTimeMillis = Optional.ofNullable(topDriverRanking)
                .map(DriverRanking::getSector3Millis)
                .orElse(0L);

        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSector3Millis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getSector3Millis() - fastestTimeMillis))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getLapTimeColor(driverRanking.getSector3Millis() - fastestTimeMillis));
    }

    public static Renderer<DriverRanking> createTheoreticalBestLapTimeRenderer() {
        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getBestSectors().getTheoreticalBestLapMillis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getBestSectors().getTheoreticalBestLapMillis() - driverRanking.getLapTimeMillis()))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getTheoreticalLapTimeColor(driverRanking.getBestSectors().getTheoreticalBestLapMillis() - driverRanking.getLapTimeMillis()));
    }

    public static Renderer<DriverRanking> createBestSector1Renderer() {
        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getBestSectors().getBestSector1Millis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getBestSectors().getBestSector1Millis() - driverRanking.getSector1Millis()))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getTheoreticalLapTimeColor(driverRanking.getBestSectors().getBestSector1Millis() - driverRanking.getSector1Millis()));
    }

    public static Renderer<DriverRanking> createBestSector2Renderer() {
        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getBestSectors().getBestSector2Millis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getBestSectors().getBestSector2Millis() - driverRanking.getSector2Millis()))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getTheoreticalLapTimeColor(driverRanking.getBestSectors().getBestSector2Millis() - driverRanking.getSector2Millis()));
    }

    public static Renderer<DriverRanking> createBestSector3Renderer() {
        return LitRenderer.<DriverRanking>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, driverRanking -> FormatUtils.formatLapTime(driverRanking.getBestSectors().getBestSector3Millis()))
                .withProperty(TIMING_TEMPLATE_TIME_GAP, driverRanking -> FormatUtils.formatLapTime(driverRanking.getBestSectors().getBestSector3Millis() - driverRanking.getSector3Millis()))
                .withProperty(TIMING_TEMPLATE_COLOR, driverRanking -> getTheoreticalLapTimeColor(driverRanking.getBestSectors().getBestSector3Millis() - driverRanking.getSector3Millis()));
    }

    public static Renderer<DriverRanking> createDriverRenderer() {
        return LitRenderer.<DriverRanking>of(DRIVER_REFERENCE_TEMPLATE)
                .withProperty(DRIVER_REFERENCE_TEMPLATE_DRIVER, DriverRanking::getDriver)
                .withFunction(DRIVER_REFERENCE_TEMPLATE_CLICK_HANDLER, driverRanking -> redirectToDriverProfile(driverRanking.getDriver()));
    }

    public static Renderer<DriverRanking> createSessionRenderer() {
        return LitRenderer.<DriverRanking>of(SESSION_REFERENCE_TEMPLATE)
                .withProperty(SESSION_REFERENCE_TEMPLATE_SESSION, DriverRanking::getSession)
                .withFunction(SESSION_REFERENCE_TEMPLATE_CLICK_HANDLER, driverRanking -> redirectToSessionDetails(driverRanking.getSession()));
    }
}
