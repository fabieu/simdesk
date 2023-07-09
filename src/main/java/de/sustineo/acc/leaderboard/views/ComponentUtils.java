package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.sustineo.acc.leaderboard.entities.Session;
import de.sustineo.acc.leaderboard.utils.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ComponentUtils {
    private final BuildProperties buildProperties;

    private final String impressumUrl;
    private final String privacyUrl;

    public ComponentUtils(ApplicationContextProvider applicationContextProvider,
                          @Value("${leaderboard.links.privacy}") String privacyUrl,
                          @Value("${leaderboard.links.impressum}") String impressumUrl) {
        this.privacyUrl = privacyUrl;
        this.impressumUrl = impressumUrl;
        this.buildProperties = applicationContextProvider.getBean(BuildProperties.class);
    }

    public Component createFooter() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.add(new Paragraph("Made with ❤️ by Fabian Eulitz"));
        layout.add(new Paragraph("© " + LocalDate.now().getYear()));
        layout.add(new Paragraph("Version: " + buildProperties.getVersion()));

        if (impressumUrl != null && !impressumUrl.isEmpty()) {
            Anchor impressum = new Anchor(impressumUrl, "Impressum");
            impressum.setTarget("_blank");
            layout.add(new Paragraph(impressum));
        }

        if (privacyUrl != null && !privacyUrl.isEmpty()) {
            Anchor privacy = new Anchor(privacyUrl, "Privacy policy");
            privacy.setTarget("_blank");
            layout.add(new Paragraph(privacy));
        }

        return layout;
    }

    public static Hr createSpacer() {
        Hr hr = new Hr();
        hr.getStyle()
                .set("border-top", "1px solid var(--lumo-contrast-10pct)");

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
