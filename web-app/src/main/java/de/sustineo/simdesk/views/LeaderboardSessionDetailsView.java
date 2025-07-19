package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.LeaderboardLine;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.SessionType;
import de.sustineo.simdesk.entities.comparator.LeaderboardLineLapTimeComparator;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.services.leaderboard.LeaderboardService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import de.sustineo.simdesk.views.components.SessionComponentFactory;
import de.sustineo.simdesk.views.generators.LeaderboardLinePartNameGenerator;
import de.sustineo.simdesk.views.generators.LeaderboardLinePodiumPartNameGenerator;
import de.sustineo.simdesk.views.renderers.LeaderboardLineRenderer;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.stream.Collectors;

@Log
@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/sessions/:fileChecksum")
@PageTitle("Leaderboard - Session Details")
@AnonymousAllowed
public class LeaderboardSessionDetailsView extends BaseView implements BeforeEnterObserver {
    private final SessionService sessionService;
    private final LeaderboardService leaderboardService;

    private final SessionComponentFactory sessionComponentFactory;

    public LeaderboardSessionDetailsView(SessionService sessionService,
                                         LeaderboardService leaderboardService,
                                         SessionComponentFactory sessionComponentFactory) {
        this.sessionService = sessionService;
        this.leaderboardService = leaderboardService;
        this.sessionComponentFactory = sessionComponentFactory;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        final RouteParameters routeParameters = beforeEnterEvent.getRouteParameters();

        String fileChecksum = routeParameters.get(ROUTE_PARAMETER_FILE_CHECKSUM).orElseThrow();

        try {
            Session session = sessionService.getByFileChecksum(fileChecksum);
            if (session == null) {
                throw new IllegalArgumentException("Session with file checksum " + fileChecksum + " does not exist.");
            }

            setSizeFull();
            setSpacing(false);
            setPadding(false);

            removeAll();

            add(createViewHeader());
            add(sessionComponentFactory.createSessionInformation(session));
            addAndExpand(createLeaderboardGrid(session));
            add(createFooter());
        } catch (IllegalArgumentException e) {
            beforeEnterEvent.rerouteToError(NotFoundException.class);
        }
    }

    private Component createLeaderboardGrid(Session session) {
        List<LeaderboardLine> leaderboardLines = leaderboardService.getLeaderboardLinesBySessionId(session.getId()).stream()
                .filter(sessionRanking -> sessionRanking.getLapCount() > 0)
                .collect(Collectors.toList());

        LeaderboardLine leaderboardLineWithBestTotalTime = leaderboardLines.stream()
                .findFirst()
                .orElse(LeaderboardLine.create());
        LeaderboardLine leaderboardLineWithBestLapTime = leaderboardLines.stream()
                .filter(sessionRanking -> sessionRanking.getBestLapTimeMillis() > 0)
                .min(new LeaderboardLineLapTimeComparator())
                .orElse(LeaderboardLine.create());

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
        grid.addColumn(leaderboardLine -> AccCar.getGroupById(leaderboardLine.getCarModelId()))
                .setHeader("Car Group")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(leaderboardLine -> AccCar.getModelById(leaderboardLine.getCarModelId()))
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
            grid.addColumn(LeaderboardLineRenderer.createTotalTimeRenderer(leaderboardLineWithBestTotalTime))
                    .setHeader("Total Time")
                    .setAutoWidth(true)
                    .setFlexGrow(0)
                    .setSortable(true)
                    .setComparator(LeaderboardLine::getTotalTimeMillis);
        }
        grid.addColumn(LeaderboardLineRenderer.createLapTimeRenderer(leaderboardLineWithBestLapTime))
                .setHeader("Fastest Lap")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(LeaderboardLine::getBestLapTimeMillis);

        grid.setItems(leaderboardLines);
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
}
