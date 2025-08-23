package de.sustineo.simdesk.views.components;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.streams.DownloadHandler;
import de.sustineo.simdesk.entities.LeaderboardLine;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.leaderboard.LeaderboardService;
import de.sustineo.simdesk.utils.FormatUtils;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Log
@Service
public class SessionComponentFactory extends ComponentFactory {
    private final SecurityService securityService;
    private final Optional<LeaderboardService> leaderboardService;

    public SessionComponentFactory(SecurityService securityService,
                                   Optional<LeaderboardService> leaderboardService) {
        this.securityService = securityService;
        this.leaderboardService = leaderboardService;
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
        heading.setText(session.getDescription());

        Icon weatherIcon = getWeatherIcon(session);

        Span sessionDatetimeBadge = new Span();
        sessionDatetimeBadge.setText(FormatUtils.formatDatetime(session.getSessionDatetime()));
        sessionDatetimeBadge.getElement().getThemeList().add("badge contrast");

        layout.add(weatherIcon, heading, sessionDatetimeBadge);

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN)) {
            if (leaderboardService.isPresent()) {
                DownloadHandler leaderboardLinesDownloadHandler = (event) -> {
                    String fileContent = getLeaderboardLinesAsCsv(session);

                    if (fileContent != null) {
                        event.setFileName(String.format("session_table_%s.csv", session.getFileChecksum()));
                        event.getOutputStream().write(fileContent.getBytes(StandardCharsets.UTF_8));
                    } else {
                        event.getResponse().setStatus(404);
                    }
                };
                Anchor leaderboardLinesCsvAnchor = createDownloadAnchor(leaderboardLinesDownloadHandler, "Table (CSV)");
                layout.add(leaderboardLinesCsvAnchor);
            }

            DownloadHandler fileContentDownloadHandler = (event) -> {
                String fileContent = session.getFileContent();
                if (fileContent != null) {
                    event.setFileName(String.format("session_file_%s.json", session.getFileChecksum()));
                    event.getOutputStream().write(fileContent.getBytes(StandardCharsets.UTF_8));
                } else {
                    event.getResponse().setStatus(404);
                }
            };

            Anchor sessionFileJsonAnchor = createDownloadAnchor(fileContentDownloadHandler, "File (JSON)");

            layout.add(sessionFileJsonAnchor);
        }

        return layout;
    }

    private String getLeaderboardLinesAsCsv(Session session) {
        if (leaderboardService.isEmpty()) {
            return null;
        }

        List<LeaderboardLine> leaderboardLines = leaderboardService.get().getLeaderboardLinesBySessionId(session.getId());

        try (StringWriter writer = new StringWriter()) {
            StatefulBeanToCsv<LeaderboardLine> sbc = new StatefulBeanToCsvBuilder<LeaderboardLine>(writer)
                    .build();

            sbc.write(leaderboardLines);

            return writer.toString();
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
            log.severe("An error occurred during creation of CSV resource: " + e.getMessage());
            return null;
        }
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
