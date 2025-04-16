package de.sustineo.simdesk.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.utils.FormatUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class SessionComponentFactory extends ComponentFactory {
    private final SecurityService securityService;

    public SessionComponentFactory(SecurityService securityService) {
        this.securityService = securityService;
    }

    public Icon getWeatherIcon(Session session) {
        return getWeatherIcon(session.getWetSession());
    }

    public Icon getWeatherIcon(AccSession accSession) {
        return getWeatherIcon(accSession.getSessionResult().getIsWetSession());
    }

    private Icon getWeatherIcon(boolean isWetSession) {
        Icon icon;

        if (isWetSession) {
            icon = VaadinIcon.DROP.create();
            icon.setColor("var(--weather-rainy-color)");
        } else {
            icon = VaadinIcon.SUN_O.create();
            icon.setColor("var(--weather-sunny-color)");
        }

        return icon;
    }

    public Component createSessionInformation(Session session) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.setWidthFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 heading = new H3();
        heading.setText(String.format("%s - %s - %s", session.getSessionType().getDescription(), session.getTrackName(), session.getServerName()));

        Icon weatherIcon = getWeatherIcon(session);

        Span sessionDatetimeBadge = new Span();
        sessionDatetimeBadge.setText(FormatUtils.formatDatetime(session.getSessionDatetime()));
        sessionDatetimeBadge.getElement().getThemeList().add("badge contrast");

        layout.add(weatherIcon, heading, sessionDatetimeBadge);

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN)) {
            StreamResource fileContentResource = new StreamResource(
                    String.format("session_file_%s.json", session.getFileChecksum()),
                    () -> {
                        String fileContent = session.getFileContent();
                        return new ByteArrayInputStream(fileContent != null ? fileContent.getBytes(StandardCharsets.UTF_8) : new byte[0]);
                    }
            );
            Anchor downloadSessionFileAnchor = createDownloadAnchor(fileContentResource, "File (JSON)");

            layout.add(downloadSessionFileAnchor);
        }

        return layout;
    }

    public Component createSessionInformation(AccSession accSession) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.setWidthFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 heading = new H3();
        heading.setText(String.format("%s - %s - %s", accSession.getSessionType().getDescription(), Track.getTrackNameByAccId(accSession.getTrackName()), accSession.getServerName()));

        Icon weatherIcon = getWeatherIcon(accSession);

        layout.add(weatherIcon, heading);

        return layout;
    }
}
