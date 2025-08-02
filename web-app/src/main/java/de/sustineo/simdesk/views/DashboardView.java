package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.livetiming.Dashboard;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.dashboard.DashboardService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.components.BadgeComponentFactory;
import de.sustineo.simdesk.views.components.ButtonComponentFactory;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

@Log
@Profile(ProfileManager.PROFILE_LIVE_TIMING)
@Route(value = "/dashboards")
@PageTitle("Dashboards")
@AnonymousAllowed
public class DashboardView extends BaseView {
    private final DashboardService dashboardService;
    private final SecurityService securityService;
    private final NotificationService notificationService;
    private final ButtonComponentFactory buttonComponentFactory;
    private final BadgeComponentFactory badgeComponentFactory;

    private final VerticalLayout dashboardCardLayout = new VerticalLayout();
    private final HashMap<String, Component> dashboardCardMap = new LinkedHashMap<>();

    public DashboardView(DashboardService dashboardService,
                         SecurityService securityService,
                         NotificationService notificationService,
                         ButtonComponentFactory buttonComponentFactory,
                         BadgeComponentFactory badgeComponentFactory) {
        this.dashboardService = dashboardService;
        this.securityService = securityService;
        this.notificationService = notificationService;
        this.buttonComponentFactory = buttonComponentFactory;
        this.badgeComponentFactory = badgeComponentFactory;

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
        layout.getStyle()
                .setBorder("1px solid var(--lumo-contrast-10pct)");

        Div container = new Div();
        container.setWidthFull();
        container.addClassNames("pure-g");
        container.add(createDashboardCardBasicInfo(dashboard), createDashboardCardDetails(dashboard), createDashboardCardActions(dashboard));

        layout.add(container);
        return layout;
    }

    private Component createDashboardCardBasicInfo(Dashboard dashboard) {
        Div container = new Div();
        container.addClassNames("pure-u-1", "pure-u-md-1-2");

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.getStyle()
                .setMarginBottom("var(--lumo-space-m)");

        H3 name = new H3(dashboard.getName());
        layout.add(name);

        if (dashboard.getDescription() != null) {
            Paragraph description = new Paragraph(dashboard.getDescription());
            layout.add(description);
        }

        container.add(layout);
        return container;
    }

    private Component createDashboardCardDetails(Dashboard dashboard) {
        Div container = new Div();
        container.addClassNames("pure-u-1", "pure-u-md-1-2");

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(false);
        layout.getStyle()
                .setMarginBottom("var(--lumo-space-m)");

        layout.add(createVisibilityAndLiveLayout(dashboard), createTimetableLayout(dashboard));

        container.add(layout);
        return container;
    }

    private Component createVisibilityAndLiveLayout(Dashboard dashboard) {
        Span liveBadge = badgeComponentFactory.getLiveBadge();
        if (dashboard.getStateDatetime() == null || dashboard.getStateDatetime().isBefore(Instant.now().minus(5, ChronoUnit.MINUTES))) {
            // Hide the live badge if the dashboard state datetime is null or more than 5 minutes old
            liveBadge.setVisible(false);
        }

        Span visibilityBadge = badgeComponentFactory.getVisibilityBadge(dashboard.getVisibility());

        HorizontalLayout layout = new HorizontalLayout(liveBadge, visibilityBadge);
        layout.setWidthFull();
        layout.setPadding(false);
        layout.setJustifyContentMode(JustifyContentMode.END);

        return layout;
    }

    private Component createTimetableLayout(Dashboard dashboard) {
        FlexLayout layout = new FlexLayout();
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.END);
        layout.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("gap", "var(--lumo-space-m)");

        if (dashboard.getStartDatetime() != null) {
            TextField startTimeField = new TextField("Start Time");
            startTimeField.setReadOnly(true);
            startTimeField.setValue(FormatUtils.formatDatetime(dashboard.getStartDatetime()));
            startTimeField.setTooltipText(BrowserTimeZone.get().getDisplayName(TextStyle.FULL, Locale.getDefault()));
            layout.add(startTimeField);
        }

        if (dashboard.getEndDatetime() != null) {
            TextField endTimeField = new TextField("End Time");
            endTimeField.setReadOnly(true);
            endTimeField.setValue(FormatUtils.formatDatetime(dashboard.getEndDatetime()));
            endTimeField.setTooltipText(BrowserTimeZone.get().getDisplayName(TextStyle.FULL, Locale.getDefault()));
            layout.add(endTimeField);
        }

        return layout;
    }

    private Component createDashboardCardActions(Dashboard dashboard) {
        Div container = new Div();
        container.addClassNames("pure-u-1");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setPadding(false);

        Button editButton = buttonComponentFactory.createWarningButton("Edit");

        Button viewButton = buttonComponentFactory.createPrimarySuccessButton("View");
        viewButton.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate(DashboardView.class, new RouteParam(ROUTE_PARAMETER_DASHBOARD_ID, dashboard.getId())));
        });

        Button deleteButton = buttonComponentFactory.createErrorButton("Delete");

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_MANAGER)) {
            layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            layout.add(editButton, viewButton, deleteButton);
        } else {
            layout.setJustifyContentMode(JustifyContentMode.CENTER);
            layout.add(viewButton);
        }

        container.add(layout);
        return container;
    }

    private void reloadDashboardCards() {
        dashboardCardLayout.removeAll();
        dashboardCardLayout.add(dashboardCardMap.values());
    }
}
