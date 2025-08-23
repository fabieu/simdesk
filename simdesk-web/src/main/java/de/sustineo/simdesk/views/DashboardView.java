package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.Style;
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
import de.sustineo.simdesk.views.forms.DashboardEditForm;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Profile(ProfileManager.PROFILE_LIVE_TIMING)
@Route(value = "/dashboards")
@AnonymousAllowed
public class DashboardView extends BaseView {
    private final DashboardService dashboardService;
    private final SecurityService securityService;
    private final NotificationService notificationService;
    private final ButtonComponentFactory buttonComponentFactory;
    private final BadgeComponentFactory badgeComponentFactory;

    private final VerticalLayout dashboardCardListLayout = new VerticalLayout();
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
        addAndExpand(createDashboardLayout());
    }

    @Override
    public String getPageTitle() {
        return "Dashboards";
    }

    private boolean hasManagementAccess() {
        return securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN);
    }

    private Component createDashboardLayout() {
        Div container = new Div();
        container.addClassNames("container", "bg-light");
        container.getStyle()
                .setPadding("0");

        dashboardCardListLayout.setPadding(false);

        reloadDashboardCards();

        VerticalLayout dashboardLayout = new VerticalLayout();
        dashboardLayout.add(dashboardCardListLayout);

        if (hasManagementAccess()) {
            dashboardLayout.add(createDashboardActions());
        }

        container.add(dashboardLayout);
        return container;
    }

    private void reloadDashboardCards() {
        dashboardCardMap.clear();
        dashboardCardListLayout.removeAll();

        List<Dashboard> dashboardList = dashboardService.findAll();
        if (dashboardList.isEmpty()) {
            notificationService.showInfoNotification("No dashboards available.");
        } else {
            dashboardList.forEach(dashboard -> {
                Component dashboardCard = createDashboardCard(dashboard);
                dashboardCardMap.put(dashboard.getId(), dashboardCard);
            });
        }

        dashboardCardListLayout.add(dashboardCardMap.values());
    }

    private Component createDashboardActions() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);

        Button createDashboardButton = buttonComponentFactory.createPrimarySuccessButton("Create new dashboard");
        createDashboardButton.addClickListener(event -> createNewDashboardDialog().open());

        layout.add(createDashboardButton);
        return layout;
    }

    private Component createDashboardCard(Dashboard dashboard) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.getStyle()
                .setBorder("1px solid var(--lumo-contrast-10pct)");

        layout.add(createDashboardCardHeader(dashboard));

        if (dashboard.getDescription() != null) {
            layout.add(createDashboardCardDescription(dashboard.getDescription()));
        }

        if (dashboard.getStartDatetime() != null || dashboard.getEndDatetime() != null) {
            layout.add(createTimetable(dashboard.getStartDatetime(), dashboard.getEndDatetime()));
        }

        layout.add(createDashboardCardActions(dashboard));

        return layout;
    }

    private Component createDashboardCardHeader(Dashboard dashboard) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();

        H3 name = new H3(dashboard.getName());
        Span liveBadge = badgeComponentFactory.getLiveBadge(dashboard);
        Span visibilityBadge = badgeComponentFactory.getVisibilityBadge(dashboard.getVisibility());

        layout.add(name, visibilityBadge, liveBadge);
        return layout;
    }

    private Component createDashboardCardDescription(String description) {
        Markdown markdown = new Markdown(description);
        markdown.setWidthFull();
        return markdown;
    }

    private Component createTimetable(Instant startDatetime, Instant endDatetime) {
        FlexLayout layout = new FlexLayout();
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("gap", "var(--lumo-space-m)");

        if (startDatetime != null) {
            TextField startTimeField = new TextField("Start Time");
            startTimeField.setReadOnly(true);
            startTimeField.setValue(FormatUtils.formatDatetime(startDatetime));
            startTimeField.setTooltipText(BrowserTimeZone.getDisplayName());
            layout.add(startTimeField);
        }

        if (endDatetime != null) {
            TextField endTimeField = new TextField("End Time");
            endTimeField.setReadOnly(true);
            endTimeField.setValue(FormatUtils.formatDatetime(endDatetime));
            endTimeField.setTooltipText(BrowserTimeZone.getDisplayName());
            layout.add(endTimeField);
        }

        return layout;
    }

    private Component createDashboardCardActions(Dashboard dashboard) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setPadding(false);

        Button editButton = buttonComponentFactory.createWarningButton("Edit");
        editButton.addClickListener(event -> createEditDashboardDialog(dashboard).open());

        Button viewButton = buttonComponentFactory.createPrimarySuccessButton("View");
        viewButton.addClickListener(event -> getUI()
                .ifPresent(ui -> ui.navigate(DashboardDetailedView.class, new RouteParam(ROUTE_PARAMETER_DASHBOARD_ID, dashboard.getId()))));


        ConfirmDialog deleteConfirmDialog = createDeleteConfirmDialog(dashboard);
        Button deleteButton = buttonComponentFactory.createErrorButton("Delete");
        deleteButton.addClickListener(event -> deleteConfirmDialog.open());

        if (hasManagementAccess()) {
            layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            layout.add(editButton, viewButton, deleteButton);
        } else {
            layout.setJustifyContentMode(JustifyContentMode.CENTER);
            layout.add(viewButton);
        }

        return layout;
    }

    private ConfirmDialog createDeleteConfirmDialog(Dashboard dashboard) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Delete dashboard: " + dashboard.getName());
        confirmDialog.setText(String.format("""
                Are you sure you want to delete the dashboard: %s?
                This action cannot be undone!"
                """, dashboard.getName()));
        confirmDialog.setConfirmText("Permanently delete");
        confirmDialog.setConfirmButtonTheme("primary error");
        confirmDialog.addConfirmListener(event -> deleteDashboard(dashboard));
        confirmDialog.setCancelable(true);
        confirmDialog.setCancelButtonTheme("tertiary error");
        return confirmDialog;
    }

    private void deleteDashboard(Dashboard dashboard) {
        Component dashboardCard = dashboardCardMap.remove(dashboard.getId());
        if (dashboardCard != null) {
            dashboardService.deleteDashboard(dashboard.getId());

            dashboardCard.removeFromParent();
            notificationService.showSuccessNotification("Successfully deleted dashboard: " + dashboard.getName());
        }
    }

    private Dialog createNewDashboardDialog() {
        Dashboard dashboard = dashboardService.createDashboard();
        return createEditDashboardDialog(dashboard);
    }

    private Dialog createEditDashboardDialog(Dashboard dashboard) {
        Dialog dialog = new Dialog();
        dialog.setResizable(true);
        dialog.setWidth("800px");

        DashboardEditMode editMode;
        if (dashboard.getName() == null) {
            editMode = DashboardEditMode.CREATE;
        } else {
            editMode = DashboardEditMode.UPDATE;
        }

        switch (editMode) {
            case CREATE -> dialog.setHeaderTitle("Create new dashboard");
            case UPDATE -> dialog.setHeaderTitle("Edit dashboard: " + dashboard.getName());
        }

        Button saveButton = buttonComponentFactory.createPrimarySuccessButton("Save");
        saveButton.setEnabled(false);

        DashboardEditForm dashboardEditForm = new DashboardEditForm(dashboard);
        Binder<Dashboard> binder = dashboardEditForm.getBinder();
        binder.addStatusChangeListener(event -> saveButton.setEnabled(binder.isValid()));

        saveButton.addClickListener(event -> {
            try {
                binder.writeBean(dashboard);
                dashboardService.upsertDashboard(dashboard);

                reloadDashboardCards();
                dialog.close();

                switch (editMode) {
                    case CREATE ->
                            notificationService.showSuccessNotification("Successfully created new dashboard: " + dashboard.getName());
                    case UPDATE ->
                            notificationService.showSuccessNotification("Successfully updated dashboard: " + dashboard.getName());
                }
            } catch (Exception e) {
                notificationService.showErrorNotification("Failed to save dashboard: " + e.getMessage());
            }
        });

        dialog.add(dashboardEditForm);
        dialog.getFooter().add(buttonComponentFactory.createDialogCancelButton(dialog), saveButton);

        return dialog;
    }

    private enum DashboardEditMode {
        CREATE,
        UPDATE,
    }
}
