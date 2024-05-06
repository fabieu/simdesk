package de.sustineo.simdesk.views;

import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.sustineo.simdesk.entities.Session;
import org.springframework.stereotype.Service;

@Service
public class ComponentUtils {
    public static final String GRID_RANKING_WIDTH = "70px";

    public static Hr createSpacer() {
        String color = "var(--lumo-contrast-10pct)";

        Hr hr = new Hr();
        hr.getStyle()
                .setMargin("var(--lumo-space-s) calc(var(--lumo-border-radius-m) / 2)")
                .set("border-top", "1px solid " + color);

        return hr;
    }

    public static Icon createWeatherIcon(Session session) {
        Icon icon;

        if (session.getWetSession()) {
            icon = VaadinIcon.DROP.create();
            icon.setColor("var(--weather-rainy-color)");
        } else {
            icon = VaadinIcon.SUN_O.create();
            icon.setColor("var(--weather-sunny-color)");
        }

        return icon;
    }
}
