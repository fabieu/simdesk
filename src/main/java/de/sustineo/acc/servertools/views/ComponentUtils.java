package de.sustineo.acc.servertools.views;

import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.sustineo.acc.servertools.entities.Session;
import org.springframework.stereotype.Service;

@Service
public class ComponentUtils {
    public static Hr createSpacer(String color) {
        Hr hr = new Hr();
        hr.getStyle()
                .setMargin("var(--lumo-space-m) calc(var(--lumo-border-radius-m) / 2)")
                .set("border-top", "1px solid " + color);

        return hr;
    }

    public static Hr createSpacer(){
        return createSpacer("var(--lumo-contrast-10pct)");
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
