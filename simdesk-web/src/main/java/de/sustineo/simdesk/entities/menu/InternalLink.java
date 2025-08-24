package de.sustineo.simdesk.entities.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.RouterLink;

public record InternalLink(Class<? extends Component> navigationTarget) implements NavigationTarget {
    @Override
    public void applyTo(Button button) {
        button.addClickListener(e -> button.getUI().ifPresent(ui -> ui.navigate(navigationTarget)));
    }

    @Override
    public Component asComponent(String text) {
        return new RouterLink(text, navigationTarget);
    }
}