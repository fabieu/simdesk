package de.sustineo.acc.servertools.views;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.configuration.VaadinConfiguration;
import de.sustineo.acc.servertools.entities.Session;
import de.sustineo.acc.servertools.entities.comparator.SessionRankingLapTimeComparator;
import de.sustineo.acc.servertools.entities.enums.SessionType;
import de.sustineo.acc.servertools.entities.ranking.SessionRanking;
import de.sustineo.acc.servertools.layouts.MainLayout;
import de.sustineo.acc.servertools.services.leaderboard.RankingService;
import de.sustineo.acc.servertools.services.leaderboard.SessionService;
import de.sustineo.acc.servertools.utils.FormatUtils;
import de.sustineo.acc.servertools.views.generators.SessionRankingDNFNameGenerator;
import de.sustineo.acc.servertools.views.generators.SessionRankingPodiumPartNameGenerator;
import de.sustineo.acc.servertools.views.renderers.ranking.SessionRankingRenderer;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

@Log
@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/sessions/:fileChecksum", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_SHORT_PREFIX + "Leaderboard - Session")
@AnonymousAllowed
public class SessionRankingView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE_PARAMETER_FILE_CHECKSUM = "fileChecksum";
    private final RankingService rankingService;
    private final SessionService sessionService;
    private GridListDataView<SessionRanking> dataView;

    public SessionRankingView(RankingService rankingService, SessionService sessionService) {
        this.rankingService = rankingService;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(false);
    }


    private Component createSessionInformation(String fileChecksum) {
        Session session = sessionService.getSession(fileChecksum);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(true);

        // Session Information Layout
        HorizontalLayout sessionInformationLayout = new HorizontalLayout();
        sessionInformationLayout.setWidthFull();
        sessionInformationLayout.setAlignItems(Alignment.CENTER);

        H3 heading = new H3();
        heading.setText(String.format("%s - %s - %s", session.getSessionType().getDescription(), session.getTrackName(), session.getServerName()));

        Icon weatherIcon = ComponentUtils.createWeatherIcon(session);

        Span sessionDatetimeBadge = new Span();
        sessionDatetimeBadge.setText(FormatUtils.formatDatetime(session.getSessionDatetime()));
        sessionDatetimeBadge.getElement().getThemeList().add("badge contrast");

        sessionInformationLayout.add(weatherIcon, heading, sessionDatetimeBadge);

        // Action Layout
        HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setWidthFull();
        actionLayout.setAlignItems(Alignment.CENTER);

        StreamResource csvResource = new StreamResource(
                String.format("session_export_%s.csv", fileChecksum),
                () -> {
                    String csv = this.exportCSV();
                    return new ByteArrayInputStream(csv != null ? csv.getBytes(StandardCharsets.UTF_8) : new byte[0]);
                }
        );

        Anchor exportAnchor = new Anchor(csvResource, "");
        exportAnchor.getElement().setAttribute("download", true);
        exportAnchor.removeAll();
        exportAnchor.add(new Button("Download CSV", new Icon(VaadinIcon.CLOUD_DOWNLOAD_O)));

        actionLayout.add(exportAnchor);

        layout.add(sessionInformationLayout, actionLayout);
        return layout;
    }

    private Component createLeaderboardGrid(String fileChecksum) {
        List<SessionRanking> sessionRankings = rankingService.getSessionRanking(fileChecksum);
        List<SessionRanking> filteredSessionRankings = sessionRankings.stream()
                .filter(sessionRanking -> sessionRanking.getLapCount() > 0 && sessionRanking.getBestLapTimeMillis() > 0)
                .toList();
        SessionRanking bestTotalTimeSessionRanking = filteredSessionRankings.stream().findFirst().orElse(new SessionRanking());
        SessionRanking bestLapTimeSessionRanking = filteredSessionRankings.stream()
                .min(new SessionRankingLapTimeComparator())
                .orElse(new SessionRanking());

        Grid<SessionRanking> grid = new Grid<>(SessionRanking.class, false);
        grid.addColumn(SessionRanking::getRanking)
                .setHeader("#")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setPartNameGenerator(new SessionRankingPodiumPartNameGenerator());
        grid.addColumn(SessionRankingRenderer.createRaceNumberRenderer())
                .setHeader("Race Number")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(SessionRanking::getRaceNumber);
        grid.addColumn(SessionRanking::getCarGroup)
                .setHeader("Car Group")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(SessionRanking::getCarModelName)
                .setHeader("Car Model")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(SessionRankingRenderer.createDriversRenderer())
                .setHeader("Drivers")
                .setSortable(true);
        grid.addColumn(SessionRanking::getLapCount)
                .setHeader("Laps")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        if (SessionType.R.equals(bestTotalTimeSessionRanking.getSession().getSessionType())) {
            grid.addColumn(SessionRankingRenderer.createTotalTimeRenderer(bestTotalTimeSessionRanking))
                    .setHeader("Total Time")
                    .setAutoWidth(true)
                    .setFlexGrow(0)
                    .setSortable(true)
                    .setComparator(SessionRanking::getTotalTimeMillis);
        }
        grid.addColumn(SessionRankingRenderer.createLapTimeRenderer(bestLapTimeSessionRanking))
                .setHeader("Fastest Lap")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(SessionRanking::getBestLapTimeMillis);

        dataView = grid.setItems(filteredSessionRankings);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setPartNameGenerator(new SessionRankingDNFNameGenerator(bestTotalTimeSessionRanking));

        return grid;
    }

    private String exportCSV() {
        Stream<SessionRanking> sessionRows = dataView.getItems();

        try (StringWriter writer = new StringWriter()) {
            StatefulBeanToCsv<SessionRanking> sbc = new StatefulBeanToCsvBuilder<SessionRanking>(writer)
                    .withSeparator(';')
                    .build();

            sbc.write(sessionRows);

            return writer.toString();
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
            log.severe("An error occurred during creation of CSV resource: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final RouteParameters routeParameters = event.getRouteParameters();

        String fileChecksum = routeParameters.get(ROUTE_PARAMETER_FILE_CHECKSUM).orElseThrow();

        try {
            if (!sessionService.sessionExistsByFileChecksum(fileChecksum)) {
                throw new IllegalArgumentException("Session with file checksum " + fileChecksum + " does not exist.");
            }

            add(createSessionInformation(fileChecksum));
            addAndExpand(createLeaderboardGrid(fileChecksum));
        } catch (IllegalArgumentException e) {
            event.rerouteToError(NotFoundException.class);
        }
    }
}
