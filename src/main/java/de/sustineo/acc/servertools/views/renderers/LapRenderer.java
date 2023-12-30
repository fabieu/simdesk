package de.sustineo.acc.servertools.views.renderers;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import de.sustineo.acc.servertools.entities.Lap;
import de.sustineo.acc.servertools.utils.FormatUtils;

public class LapRenderer extends GridRenderer {
    public static Renderer<Lap> createLapTimeRenderer() {
        return LitRenderer.<Lap>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, lap -> FormatUtils.formatLapTime(lap.getLapTimeMillis()));
    }

    public static Renderer<Lap> createSplit1Renderer() {
        return LitRenderer.<Lap>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, lap -> FormatUtils.formatLapTime(lap.getSplit1Millis()));
    }

    public static Renderer<Lap> createSplit2Renderer() {
        return LitRenderer.<Lap>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, lap -> FormatUtils.formatLapTime(lap.getSplit2Millis()));
    }

    public static Renderer<Lap> createSplit3Renderer() {
        return LitRenderer.<Lap>of(TIMING_TEMPLATE)
                .withProperty(TIMING_TEMPLATE_TIME, lap -> FormatUtils.formatLapTime(lap.getSplit3Millis()));
    }

}
