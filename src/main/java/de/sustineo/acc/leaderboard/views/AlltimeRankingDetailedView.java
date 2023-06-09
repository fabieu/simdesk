package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.Track;
import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import lombok.extern.java.Log;
import org.apache.commons.lang3.EnumUtils;

@Log
@Route(value = "ranking/all-time/:carGroup/:trackId", layout = MainView.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Global Ranking")
@AnonymousAllowed
public class AlltimeRankingDetailedView extends VerticalLayout implements BeforeEnterObserver {

    public AlltimeRankingDetailedView() {
        addClassName("alltime-ranking-detailed-view");
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final RouteParameters routeParameters = event.getRouteParameters();

        String carGroup = routeParameters.get("carGroup").orElseThrow();
        String trackId = routeParameters.get("trackId").orElseThrow();

        if (Track.isTrackIdValid(trackId) && EnumUtils.isValidEnum(CarGroup.class, carGroup.toUpperCase())) {
            add(createRankingGrid(carGroup, trackId));
        } else {
            event.rerouteToError(NotFoundException.class);
        }
    }

    private Component createRankingGrid(String carGroup, String trackId) {
        return new Grid<>();
    }
}
