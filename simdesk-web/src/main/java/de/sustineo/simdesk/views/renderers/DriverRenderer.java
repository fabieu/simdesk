package de.sustineo.simdesk.views.renderers;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import de.sustineo.simdesk.entities.Driver;

public class DriverRenderer extends GridRenderer {
    public static Renderer<Driver> createRealDriverRenderer() {
        return LitRenderer.<Driver>of(DRIVER_REALNAME_REFERENCE_TEMPLATE)
                .withProperty(DRIVER_REFERENCE_TEMPLATE_DRIVER, driver -> driver)
                .withFunction(DRIVER_REFERENCE_TEMPLATE_CLICK_HANDLER, GridRenderer::redirectToDriverProfile);
    }
}
