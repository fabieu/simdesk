package de.sustineo.simdesk.views.renderers;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import de.sustineo.simdesk.entities.bop.Bop;

public class BopRenderer extends GridRenderer {
    public static Renderer<Bop> createBallastKgRenderer() {
        return LitRenderer.<Bop>of(BOP_BALLAST_TEMPLATE)
                .withProperty(BOP_BALLAST_TEMPLATE_BALLAST_KG, bop -> enrichNumber(bop.getBallastKg()));
    }

    public static Renderer<Bop> createRestrictorRenderer() {
        return LitRenderer.<Bop>of(BOP_RESTRICTOR_TEMPLATE)
                .withProperty(BOP_RESTRICTOR_TEMPLATE_RESTRICTOR, bop -> enrichNumber(bop.getRestrictor()));
    }
}
