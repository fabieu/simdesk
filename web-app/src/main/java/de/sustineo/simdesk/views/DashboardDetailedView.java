package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.livetiming.Dashboard;
import de.sustineo.simdesk.services.dashboard.DashboardService;
import de.sustineo.simdesk.views.components.BadgeComponentFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;

@Profile(ProfileManager.PROFILE_LIVE_TIMING)
@Route(value = "/dashboards/:dashboardId")
@PageTitle("Dashboards")
@AnonymousAllowed
@RequiredArgsConstructor
public class DashboardDetailedView extends BaseView implements BeforeEnterObserver {
    private final DashboardService dashboardService;
    private final BadgeComponentFactory badgeComponentFactory;

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String dashboardId = beforeEnterEvent.getRouteParameters().get(ROUTE_PARAMETER_DASHBOARD_ID).orElseThrow();

        try {
            Dashboard dashboard = dashboardService.getByDashboardId(dashboardId);
            if (dashboard == null) {
                throw new IllegalArgumentException("Dashboard with dashboard id " + dashboardId + " does not exist.");
            }

            setSizeFull();
            setSpacing(false);
            setPadding(false);

            removeAll();

            add(createViewHeader(dashboard.getName(), badgeComponentFactory.getLiveBadge(dashboard)));
            addAndExpand(createDashboardLayout(dashboard));
        } catch (IllegalArgumentException e) {
            beforeEnterEvent.rerouteToError(NotFoundException.class);
        }
    }

    private Component createDashboardLayout(Dashboard dashboard) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        return layout;
    }
}
