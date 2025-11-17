package de.sustineo.simdesk.views.components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.ThemeList;
import de.sustineo.simdesk.entities.Visibility;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BadgeComponentFactory extends ComponentFactory {
    public Span getVisibilityBadge(@Nonnull Visibility visibility) {
        Objects.requireNonNull(visibility);

        Span badge = new Span(visibility.name());
        badge.addClassName("badge-" + visibility.name().toLowerCase());

        ThemeList themeList = badge.getElement().getThemeList();
        themeList.add("badge");

        switch (visibility) {
            case PUBLIC -> themeList.add("success");
            case PRIVATE -> themeList.add("error");
            case UNLISTED -> themeList.add("contrast");
        }

        return badge;
    }
}
