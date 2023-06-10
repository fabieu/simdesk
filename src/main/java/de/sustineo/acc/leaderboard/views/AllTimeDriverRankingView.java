package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.DriverRanking;
import de.sustineo.acc.leaderboard.entities.Track;
import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import de.sustineo.acc.leaderboard.services.RankingService;
import de.sustineo.acc.leaderboard.views.generators.PodiumPartNameGenerator;

import java.util.List;

@Route(value = "ranking/all-time/:carGroup/:trackId", layout = MainView.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "All Time Ranking")
@AnonymousAllowed
public class AllTimeDriverRankingView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE_PARAMETER_CAR_GROUP = "carGroup";
    public static final String ROUTE_PARAMETER_TRACK_ID = "trackId";
    private final RankingService rankingService;

    public AllTimeDriverRankingView(RankingService rankingService) {
        this.rankingService = rankingService;
        addClassName("alltime-ranking-detailed-view");
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final RouteParameters routeParameters = event.getRouteParameters();

        String carGroup = routeParameters.get(ROUTE_PARAMETER_CAR_GROUP).orElseThrow();
        String trackId = routeParameters.get(ROUTE_PARAMETER_TRACK_ID).orElseThrow();

        if (Track.isValid(trackId) && CarGroup.isValid(carGroup)) {
            add(createRankingGrid(carGroup, trackId));
        } else {
            event.rerouteToError(NotFoundException.class);
        }
    }

    private Component createRankingGrid(String carGroup, String trackId) {
        Grid<DriverRanking> grid = new Grid<>(DriverRanking.class, false);
        Grid.Column<DriverRanking> rankingColumn = grid.addColumn(DriverRanking::getRanking)
                .setHeader("#")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> driverColumn = grid.addColumn(DriverRanking::getDriverFullName)
                .setHeader("Driver")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> lapTimeColumn = grid.addColumn(DriverRanking::getLapTime)
                .setHeader("Lap Time")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setPartNameGenerator(ranking -> "font-weight-bold")
                .setSortable(true);
        Grid.Column<DriverRanking> split1Column = grid.addColumn(DriverRanking::getSplit1)
                .setHeader("Split 1")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> split2Column = grid.addColumn(DriverRanking::getSplit2)
                .setHeader("Split 2")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> split3Column = grid.addColumn(DriverRanking::getSplit3)
                .setHeader("Split 3")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> carModelColumn = grid.addColumn(DriverRanking::getCarModelName)
                .setHeader("Car Model")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> sessionColumn = grid.addColumn(DriverRanking::getSessionDescription)
                .setHeader("Session")
                .setResizable(true);

        List<DriverRanking> driverRankings = rankingService.getAllTimeDriverRanking(carGroup, trackId);
        grid.setItems(driverRankings);
        grid.setHeightFull();
        grid.setMultiSort(true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setPartNameGenerator(new PodiumPartNameGenerator());

        return grid;
    }
}
