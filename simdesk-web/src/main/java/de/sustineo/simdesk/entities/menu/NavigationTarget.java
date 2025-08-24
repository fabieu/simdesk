package de.sustineo.simdesk.entities.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;

public sealed interface NavigationTarget permits ExternalLink, InternalLink {
    void applyTo(Button button);

    Component asComponent(String text);
}