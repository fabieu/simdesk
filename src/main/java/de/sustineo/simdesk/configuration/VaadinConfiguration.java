package de.sustineo.simdesk.configuration;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

/**
 * An interface to configure application features and the host page where the Vaadin application is running.
 * It automatically configures the index.html page.
 */
@Viewport("width=device-width, initial-scale=1")
@Meta(name = "Author", content = "Fabian Eulitz")
@Meta(name = "robots", content = "noindex, nofollow")
@Meta(name= "theme-color", content= "#1a1a1a")
@PWA(name = "SimDesk", shortName = "SimDesk", description = "SimDesk - Sim Racing Utilities")
@Theme(value = "default")
public class VaadinConfiguration implements AppShellConfigurator {
    public static final String APPLICATION_NAME = "SimDesk";
    public static final String APPLICATION_NAME_SHORT_PREFIX = APPLICATION_NAME + " - ";
}