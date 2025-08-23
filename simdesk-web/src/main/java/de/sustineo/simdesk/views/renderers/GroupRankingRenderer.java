package de.sustineo.simdesk.views.renderers;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import de.sustineo.simdesk.entities.ranking.GroupRanking;

public class GroupRankingRenderer extends GridRenderer {
    public static Renderer<GroupRanking> createDriverRenderer() {
        return LitRenderer.<GroupRanking>of(DRIVER_REFERENCE_TEMPLATE)
                .withProperty(DRIVER_REFERENCE_TEMPLATE_DRIVER, GroupRanking::getDriver)
                .withFunction(DRIVER_REFERENCE_TEMPLATE_CLICK_HANDLER, groupRanking -> redirectToDriverProfile(groupRanking.getDriver()));
    }
}
