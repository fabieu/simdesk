package de.sustineo.simdesk.views.components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.ThemeList;
import de.sustineo.simdesk.entities.Visibility;
import de.sustineo.simdesk.entities.livetiming.Dashboard;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    public Span getLiveBadge(Dashboard dashboard) {
        Icon icon = new Icon(VaadinIcon.EXCLAMATION_CIRCLE_O);
        icon.getStyle().setPadding("var(--lumo-space-xs)");

        Span badge = new Span(icon, new Span("LIVE"));
        badge.getElement().getThemeList().add("badge primary error");
        badge.addClassName("badge-live");

        // Hide badge if the dashboard state datetime is null or more than 5 minutes old
        if (dashboard.getStateDatetime() == null || dashboard.getStateDatetime().isBefore(Instant.now().minus(5, ChronoUnit.MINUTES))) {
            badge.setVisible(false);
        }

        return badge;
    }
}
