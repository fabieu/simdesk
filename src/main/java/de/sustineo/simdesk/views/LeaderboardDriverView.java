package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.services.leaderboard.DriverService;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

@Log
@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/drivers/:driverId")
@PageTitle("Leaderboard - Driver")
@AnonymousAllowed
public class LeaderboardDriverView extends BaseView implements BeforeEnterObserver {
    private final DriverService driverService;

    public LeaderboardDriverView(DriverService driverService) {
        this.driverService = driverService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        final RouteParameters routeParameters = beforeEnterEvent.getRouteParameters();

        String driverId = routeParameters.get(ROUTE_PARAMETER_DRIVER_ID).orElseThrow();

        try {
            Driver driver = driverService.getDriverById(driverId);
            if (driver == null) {
                throw new IllegalArgumentException("Driver with driver id " + driverId + " does not exist.");
            }

            setSizeFull();
            setSpacing(false);
            setPadding(false);

            add(createViewHeader(driver.getFullName()));
            addAndExpand(createDriverLayout(driver));
            add(createFooter());
        } catch (IllegalArgumentException e) {
            beforeEnterEvent.rerouteToError(NotFoundException.class);
        }
    }

    private Component createDriverLayout(Driver driver) {
        VerticalLayout layout = new VerticalLayout();
        return layout;
    }
}
