package de.sustineo.simdesk.views;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.SessionType;
import de.sustineo.simdesk.entities.auth.Role;
import de.sustineo.simdesk.entities.comparator.SessionRankingLapTimeComparator;
import de.sustineo.simdesk.entities.ranking.SessionRanking;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.leaderboard.RankingService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.generators.SessionRankingDNFNameGenerator;
import de.sustineo.simdesk.views.generators.SessionRankingPodiumPartNameGenerator;
import de.sustineo.simdesk.views.renderers.SessionRankingRenderer;
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
@PageTitle("Leaderboard - Session Details")
@AnonymousAllowed
public class LeaderboardSessionDetailsView extends BaseView implements BeforeEnterObserver {
    private final RankingService rankingService;
    private final SessionService sessionService;
    private final SecurityService securityService;

    private GridListDataView<SessionRanking> dataView;

    public LeaderboardSessionDetailsView(RankingService rankingService,
                                         SessionService sessionService,
                                         SecurityService securityService) {
        this.rankingService = rankingService;
        this.sessionService = sessionService;
        this.securityService = securityService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        final RouteParameters routeParameters = beforeEnterEvent.getRouteParameters();

        String fileChecksum = routeParameters.get(ROUTE_PARAMETER_FILE_CHECKSUM).orElseThrow();

        try {
            Session session = sessionService.getSession(fileChecksum);
            if (session == null) {
                throw new IllegalArgumentException("Session with file checksum " + fileChecksum + " does not exist.");
            }

            setSizeFull();
            setSpacing(false);
            setPadding(false);

            add(createViewHeader());
            add(createSessionInformation(session));
            addAndExpand(createLeaderboardGrid(session));
            add(createFooter());
        } catch (IllegalArgumentException e) {
            beforeEnterEvent.rerouteToError(NotFoundException.class);
        }
    }

    private Component createSessionInformation(Session session) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);

        H3 heading = new H3();
        heading.setText(String.format("%s - %s - %s", session.getSessionType().getDescription(), session.getTrackName(), session.getServerName()));

        Icon weatherIcon = getWeatherIcon(session);

        Span sessionDatetimeBadge = new Span();
        sessionDatetimeBadge.setText(FormatUtils.formatDatetime(session.getSessionDatetime()));
        sessionDatetimeBadge.getElement().getThemeList().add("badge contrast");

        layout.add(weatherIcon, heading, sessionDatetimeBadge);

        if (securityService.hasAnyRole(Role.ADMIN)) {
            StreamResource csvResource = new StreamResource(
                    String.format("session_table_%s.csv", session.getFileChecksum()),
                    () -> {
                        String csv = this.exportCSV();
                        return new ByteArrayInputStream(csv != null ? csv.getBytes(StandardCharsets.UTF_8) : new byte[0]);
                    }
            );
            Anchor downloadSessionAnchor = createDownloadAnchor(csvResource, "Table (CSV)");

            StreamResource fileContentResource = new StreamResource(
                    String.format("session_file_%s.json", session.getFileChecksum()),
                    () -> {
                        String fileContent = session.getFileContent();
                        return new ByteArrayInputStream(fileContent != null ? fileContent.getBytes(StandardCharsets.UTF_8) : new byte[0]);
                    }
            );
            Anchor downloadSessionFileAnchor = createDownloadAnchor(fileContentResource, "File (JSON)");

            layout.add(downloadSessionAnchor, downloadSessionFileAnchor);
        }

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
                .setWidth(GRID_RANKING_WIDTH)
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
                getUI().ifPresent(ui -> ui.navigate(LeaderboardSessionCarDetailsView.class,
                        new RouteParameters(
                                new RouteParam(ROUTE_PARAMETER_FILE_CHECKSUM, session.getFileChecksum()),
                                new RouteParam(ROUTE_PARAMETER_CAR_ID, selectedSessionRanking.getCarId().toString())
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
}
