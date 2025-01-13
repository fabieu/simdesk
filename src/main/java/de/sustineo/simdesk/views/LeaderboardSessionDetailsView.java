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
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.*;
import de.sustineo.simdesk.entities.auth.UserRole;
import de.sustineo.simdesk.entities.comparator.LeaderboardLineLapTimeComparator;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.leaderboard.LeaderboardService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.generators.LeaderboardLinePartNameGenerator;
import de.sustineo.simdesk.views.generators.LeaderboardLinePodiumPartNameGenerator;
import de.sustineo.simdesk.views.renderers.LeaderboardLineRenderer;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/sessions/:fileChecksum")
@PageTitle("Leaderboard - Session Details")
@AnonymousAllowed
public class LeaderboardSessionDetailsView extends BaseView implements BeforeEnterObserver {
    private final SessionService sessionService;
    private final LeaderboardService leaderboardService;
    private final SecurityService securityService;

    private GridListDataView<LeaderboardLine> dataView;

    public LeaderboardSessionDetailsView(SessionService sessionService,
                                         LeaderboardService leaderboardService,
                                         SecurityService securityService) {
        this.sessionService = sessionService;
        this.leaderboardService = leaderboardService;
        this.securityService = securityService;
    }

    @Transactional
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        final RouteParameters routeParameters = beforeEnterEvent.getRouteParameters();

        String fileChecksum = routeParameters.get(ROUTE_PARAMETER_FILE_CHECKSUM).orElseThrow();

        try {
            Session session = sessionService.getSessionByFileChecksum(fileChecksum);
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
        heading.setText(String.format("%s - %s - %s", session.getSessionType().getDescription(), Track.getTrackNameByAccId(session.getTrackId()), session.getServerName()));

        Icon weatherIcon = getWeatherIcon(session);

        Span sessionDatetimeBadge = new Span();
        sessionDatetimeBadge.setText(FormatUtils.formatDatetime(session.getSessionDatetime()));
        sessionDatetimeBadge.getElement().getThemeList().add("badge contrast");

        layout.add(weatherIcon, heading, sessionDatetimeBadge);

        if (securityService.hasAnyAuthority(UserRole.ADMIN)) {
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
        List<LeaderboardLine> leaderboardLines = leaderboardService.getLeaderboardLinesBySessionId(session).stream()
                .filter(leaderboardLine -> leaderboardLine.getLapCount() > 0)
                .collect(Collectors.toList());

        LeaderboardLine bestTotalTime = leaderboardLines.stream()
                .findFirst()
                .orElse(new LeaderboardLine());
        LeaderboardLine bestLapTime = leaderboardLines.stream()
                .filter(sessionRanking -> sessionRanking.getBestLapTimeMillis() > 0)
                .min(new LeaderboardLineLapTimeComparator())
                .orElse(new LeaderboardLine());

        Grid<LeaderboardLine> grid = new Grid<>(LeaderboardLine.class, false);
        grid.addColumn(LeaderboardLine::getRanking)
                .setHeader("#")
                .setWidth(GRID_RANKING_WIDTH)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setPartNameGenerator(new LeaderboardLinePodiumPartNameGenerator());
        grid.addColumn(LeaderboardLineRenderer.createRaceNumberRenderer())
                .setHeader("Race Number")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(LeaderboardLine::getRaceNumber);
        grid.addColumn(leaderboardLine -> Car.getGroupById(leaderboardLine.getCarModelId()))
                .setHeader("Car Group")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(leaderboardLine -> Car.getNameById(leaderboardLine.getCarModelId()))
                .setHeader("Car Model")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(LeaderboardLineRenderer.createDriversRenderer())
                .setHeader("Drivers")
                .setSortable(true);
        grid.addColumn(LeaderboardLine::getLapCount)
                .setHeader("Laps")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        if (SessionType.R.equals(session.getSessionType())) {
            grid.addColumn(LeaderboardLineRenderer.createTotalTimeRenderer(bestTotalTime))
                    .setHeader("Total Time")
                    .setAutoWidth(true)
                    .setFlexGrow(0)
                    .setSortable(true)
                    .setComparator(LeaderboardLine::getTotalTimeMillis);
        }
        grid.addColumn(LeaderboardLineRenderer.createLapTimeRenderer(bestLapTime))
                .setHeader("Fastest Lap")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(LeaderboardLine::getBestLapTimeMillis);

        dataView = grid.setItems(leaderboardLines);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setPartNameGenerator(new LeaderboardLinePartNameGenerator());
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setEmptyStateText("No laps in this session!");

        SingleSelect<Grid<LeaderboardLine>, LeaderboardLine> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
            LeaderboardLine selectedLeaderboardLine = e.getValue();

            if (selectedLeaderboardLine != null) {
                getUI().ifPresent(ui -> ui.navigate(LeaderboardSessionCarDetailsView.class,
                        new RouteParameters(
                                new RouteParam(ROUTE_PARAMETER_FILE_CHECKSUM, session.getFileChecksum()),
                                new RouteParam(ROUTE_PARAMETER_CAR_ID, selectedLeaderboardLine.getCarId().toString())
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

        Stream<LeaderboardLine> sessionRows = dataView.getItems();

        try (StringWriter writer = new StringWriter()) {
            StatefulBeanToCsv<LeaderboardLine> sbc = new StatefulBeanToCsvBuilder<LeaderboardLine>(writer)
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
