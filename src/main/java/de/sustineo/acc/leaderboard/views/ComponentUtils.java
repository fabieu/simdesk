package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.sustineo.acc.leaderboard.entities.Session;

public class ComponentUtils {
    public static Icon getWeatherIcon(Session session) {
        Icon icon;

        if (session.getWetSession()) {
            icon = VaadinIcon.DROP.create();
            icon.setColor("#00aeff");
        } else {
            icon = VaadinIcon.SUN_O.create();
            icon.setColor("#ffb600");
        }

        return icon;
    }
}
