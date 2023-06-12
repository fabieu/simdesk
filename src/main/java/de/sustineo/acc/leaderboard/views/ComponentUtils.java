package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.sustineo.acc.leaderboard.entities.Session;
import de.sustineo.acc.leaderboard.utils.CustomContext;
import org.springframework.boot.info.BuildProperties;

import java.time.LocalDate;

public class ComponentUtils {
    private static final BuildProperties buildProperties = CustomContext.getBean(BuildProperties.class);

    public static Component createFooter() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.add(new Text("Made with ❤️ by Fabian Eulitz - © " + LocalDate.now().getYear() + " - Version " + buildProperties.getVersion()));
        return layout;
    }

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
