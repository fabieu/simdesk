package de.sustineo.simdesk.configuration;

import com.vaadin.flow.component.page.*;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;

import java.util.Map;

/**
 * An interface to configure application features and the host page where the Vaadin application is running.
 * It automatically configures the index.html page.
 */
@Push(PushMode.MANUAL)
@Viewport("width=device-width, initial-scale=1")
@Meta(name = "Author", content = "Fabian Eulitz")
@Meta(name = "robots", content = "noindex, nofollow")
@Meta(name = "theme-color", content = "#1a1a1a")
@PWA(name = "SimDesk", shortName = "SimDesk", description = "SimDesk - Sim Racing Utilities")
@Theme(value = "default")
public class VaadinConfiguration implements AppShellConfigurator {
    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addFavIcon("icon", "icons/favicon-48x48.png", "48x48");
        settings.addLink("shortcut icon", "icons/favicon.ico");
        settings.addLink(Inline.Position.APPEND, "icons/favicon.svg", Map.of("rel", "icon", "type", "image/svg+xml"));
        settings.addLink(Inline.Position.APPEND, "icons/apple-touch-icon.png", Map.of("rel", "apple-touch-icon", "sizes", "180x180"));
    }
}
