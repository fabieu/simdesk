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
import com.vaadin.flow.data.selection.SingleSelect;
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
        setSpacing(false);
    }


    private Component createSessionInformation(Session session) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);

        H3 heading = new H3();
        heading.setText(String.format("%s - %s - %s", session.getSessionType().getDescription(), session.getTrackName(), session.getServerName()));

        Icon weatherIcon = ComponentUtils.createWeatherIcon(session);

        Span sessionDatetimeBadge = new Span();
        sessionDatetimeBadge.setText(FormatUtils.formatDatetime(session.getSessionDatetime()));
        sessionDatetimeBadge.getElement().getThemeList().add("badge contrast");

        StreamResource csvResource = new StreamResource(
                String.format("session_export_%s.csv", session.getFileChecksum()),
                () -> {
                    String csv = this.exportCSV();
                    return new ByteArrayInputStream(csv != null ? csv.getBytes(StandardCharsets.UTF_8) : new byte[0]);
                }
        );

        Anchor csvAnchor = new Anchor(csvResource, "");
        csvAnchor.getElement().setAttribute("download", true);
        csvAnchor.removeAll();
        csvAnchor.add(new Button("CSV", new Icon(VaadinIcon.CLOUD_DOWNLOAD_O)));

        layout.add(weatherIcon, heading, sessionDatetimeBadge, csvAnchor);
        return layout;
    }

    private Component createLeaderboardGrid(Session session) {
        List<SessionRanking> sessionRankings = rankingService.getSessionRankings(session);
        List<SessionRanking> validSessionRankings = sessionRankings.stream()
                .filter(SessionRanking::isValid)
                .toList();

        if (validSessionRankings.isEmpty()) {
            VerticalLayout layout = new VerticalLayout();

            layout.setPadding(true);
            layout.setWidthFull();
            layout.setAlignItems(Alignment.CENTER);

            H3 message = new H3("No valid laps in this session!");
            message.getStyle()
                    .setColor("var(--lumo-error-text-color)");

            layout.add(message);
            return layout;
        }

        SessionRanking bestTotalTimeSessionRanking = validSessionRankings.stream().findFirst().orElse(new SessionRanking());
        SessionRanking bestLapTimeSessionRanking = validSessionRankings.stream()
                .min(new SessionRankingLapTimeComparator())
                .orElse(new SessionRanking());

        Grid<SessionRanking> grid = new Grid<>(SessionRanking.class, false);
        grid.addColumn(SessionRanking::getRanking)
                .setHeader("#")
                .setWidth(ComponentUtils.GRID_RANKING_WIDTH)
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

        dataView = grid.setItems(validSessionRankings);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setPartNameGenerator(new SessionRankingDNFNameGenerator(bestTotalTimeSessionRanking));
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        SingleSelect<Grid<SessionRanking>, SessionRanking> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
            SessionRanking selectedSessionRanking = e.getValue();

            if (selectedSessionRanking != null) {
                getUI().ifPresent(ui -> ui.navigate(SessionDetailsView.class,
                        new RouteParameters(
                                new RouteParam(SessionDetailsView.ROUTE_PARAMETER_FILE_CHECKSUM, session.getFileChecksum()),
                                new RouteParam(SessionDetailsView.ROUTE_PARAMETER_CAR_ID, selectedSessionRanking.getCarId().toString())
                        )
                ));
            }
        });

        return grid;
    }

    private String exportCSV() {
        if (dataView == null) {
            return null;
        }

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
            Session session = sessionService.getSession(fileChecksum);
            if (session == null) {
                throw new IllegalArgumentException("Session with file checksum " + fileChecksum + " does not exist.");
            }

            add(createSessionInformation(session));
            addAndExpand(createLeaderboardGrid(session));
        } catch (IllegalArgumentException e) {
            event.rerouteToError(NotFoundException.class);
        }
    }
}
