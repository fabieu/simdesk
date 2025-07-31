package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.services.dashboard.DashboardService;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

@Log
@Profile(ProfileManager.PROFILE_LIVE_TIMING)
@Route(value = "/dashboards")
@PageTitle("Dashboard - Management")
@RolesAllowed({"ADMIN"})
public class DashboardManagementView extends BaseView {
    private final DashboardService dashboardService;

    public DashboardManagementView(DashboardService dashboardService) {
        this.dashboardService = dashboardService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        addAndExpand(createDashboardList());
    }

    private Component createDashboardList() {
        VerticalLayout layout = new VerticalLayout();
        return layout;
    }
}
