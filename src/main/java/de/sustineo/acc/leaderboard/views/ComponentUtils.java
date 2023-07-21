package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Style;
import de.sustineo.acc.leaderboard.configuration.Reference;
import de.sustineo.acc.leaderboard.entities.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ComponentUtils {
    private final String impressumUrl;
    private final String privacyUrl;

    public ComponentUtils(@Value("${leaderboard.links.privacy}") String privacyUrl,
                          @Value("${leaderboard.links.impressum}") String impressumUrl) {
        this.privacyUrl = privacyUrl;
        this.impressumUrl = impressumUrl;
    }

    public Component createFooter() {
        FlexLayout layout = new FlexLayout();
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        layout.getStyle()
                .setPosition(Style.Position.FIXED)
                .setBottom("0")
                .setPadding("var(--lumo-space-m)")
                .setBackground("var(--lumo-base-color)");

        Div creatorContainer = new Div();
        creatorContainer.add(new Text("Made with ❤️ by "));
        creatorContainer.add(new Anchor(Reference.SUSTINEO, "Fabian Eulitz", AnchorTarget.BLANK));
        creatorContainer.add(new Text(" © " + LocalDate.now().getYear()));

        HorizontalLayout referenceLayout = new HorizontalLayout();
        if (impressumUrl != null && !impressumUrl.isEmpty()) {
            referenceLayout.add(new Anchor(impressumUrl, "Impressum", AnchorTarget.BLANK));
        }

        if (privacyUrl != null && !privacyUrl.isEmpty()) {
            referenceLayout.add(new Anchor(privacyUrl, "Privacy policy", AnchorTarget.BLANK));
        }

        layout.add(creatorContainer, referenceLayout);
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
