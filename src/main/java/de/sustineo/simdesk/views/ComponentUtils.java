package de.sustineo.simdesk.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.StreamResource;
import de.sustineo.simdesk.entities.Session;
import org.springframework.stereotype.Service;

@Service
public class ComponentUtils {
    public static final String GRID_RANKING_WIDTH = "70px";

    public static AbstractIcon<?> getDownloadIcon() {
        return new Icon(VaadinIcon.CLOUD_DOWNLOAD_O);
    }

    public static AbstractIcon<?> getShareIcon() {
        return new Icon(VaadinIcon.SHARE_SQUARE);
    }

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

    public static Anchor createDownloadAnchor(StreamResource streamResource, String label) {
        Anchor anchor = new Anchor(streamResource, "");
        anchor.getElement().setAttribute("download", true);
        anchor.removeAll();
        anchor.add(new Button(label, getDownloadIcon()));
        return anchor;
    }
}
