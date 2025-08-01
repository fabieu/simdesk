package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.livetiming.Dashboard;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.dashboard.DashboardService;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Log
@Profile(ProfileManager.PROFILE_LIVE_TIMING)
@Route(value = "/dashboards")
@PageTitle("Dashboards")
@AnonymousAllowed
public class DashboardView extends BaseView {
    private final DashboardService dashboardService;
    private final NotificationService notificationService;


    private final VerticalLayout dashboardCardLayout = new VerticalLayout();
    private final HashMap<String, Component> dashboardCardMap = new LinkedHashMap<>();

    public DashboardView(DashboardService dashboardService,
                         NotificationService notificationService) {
        this.dashboardService = dashboardService;
        this.notificationService = notificationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        addAndExpand(createDashboardList());
    }

    private Component createDashboardList() {
        Div container = new Div();
        container.addClassNames("container", "bg-light");
        container.getStyle()
                .setPadding("0");

        List<Dashboard> dashboardList = dashboardService.findAll();
        if (dashboardList.isEmpty()) {
            notificationService.showInfoNotification("No dashboards available.");
        } else {
            dashboardList.forEach(dashboard -> {
                Component dashboardCard = createDashboardCard(dashboard);
                dashboardCardMap.put(dashboard.getId(), dashboardCard);
            });
        }

        reloadDashboardCards();

        container.add(dashboardCardLayout);

        return container;
    }

    private Component createDashboardCard(Dashboard dashboard) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        // Add components to display dashboard details
        layout.add(new Div(dashboard.getName()));
        layout.add(new Div(dashboard.getDescription()));
        layout.add(new Div("Visibility: " + dashboard.getVisibility()));
        layout.add(new Div("Start: " + dashboard.getStartDatetime()));
        layout.add(new Div("End: " + dashboard.getEndDatetime()));

        return layout;
    }

    private void reloadDashboardCards() {
        dashboardCardLayout.removeAll();
        dashboardCardLayout.add(dashboardCardMap.values());
    }
}
