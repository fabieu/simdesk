package de.sustineo.simdesk.entities.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;

public record ExternalLink(String href) implements NavigationTarget {
    private static final AnchorTarget ANCHOR_TARGET = AnchorTarget.BLANK;

    @Override
    public void applyTo(Button button) {
        button.addClickListener(e -> button.getUI().ifPresent(ui -> ui.getPage().open(href, ANCHOR_TARGET.getValue())));
    }

    @Override
    public Component asComponent(String text) {
        return new Anchor(href, text, ANCHOR_TARGET);
    }
}