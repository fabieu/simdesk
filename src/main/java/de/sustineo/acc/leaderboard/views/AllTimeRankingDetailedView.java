package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.DriverRanking;
import de.sustineo.acc.leaderboard.entities.Track;
import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import de.sustineo.acc.leaderboard.services.RankingService;

import java.util.List;

@Route(value = "ranking/all-time/:carGroup/:trackId", layout = MainView.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "All Time Ranking")
@AnonymousAllowed
public class AllTimeRankingDetailedView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE_PARAMETER_CAR_GROUP = "carGroup";
    public static final String ROUTE_PARAMETER_TRACK_ID = "trackId";
    private final RankingService rankingService;

    public AllTimeRankingDetailedView(RankingService rankingService) {
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
        Grid<DriverRanking> grid = new Grid<>(DriverRanking.class, true);
        List<DriverRanking> driverRankings = rankingService.getAllTimeDriverRanking(carGroup, trackId);
        grid.setItems(driverRankings);
        return grid;
    }
}
