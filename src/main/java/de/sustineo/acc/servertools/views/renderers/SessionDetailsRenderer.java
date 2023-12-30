package de.sustineo.acc.servertools.views.renderers;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import de.sustineo.acc.servertools.entities.Penalty;

public class SessionDetailsRenderer extends GridRenderer {
    public static Renderer<Penalty> createPenaltyServedRenderer() {
        return LitRenderer.<Penalty>of(PENALTY_SERVED_TEMPLATE)
                .withProperty(PENALTY_SERVED_TEMPLATE_BOOLEAN, Penalty::getPostRace);
    }
}
