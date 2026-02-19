package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.stewarding.*;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.stewarding.PenaltyCatalogService;
import de.sustineo.simdesk.services.stewarding.RaceWeekendService;
import de.sustineo.simdesk.services.stewarding.StewardingIncidentService;
import de.sustineo.simdesk.services.stewarding.StewardingTrackService;
import de.sustineo.simdesk.views.BaseView;
import org.springframework.context.annotation.Profile;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/weekends/:weekendId", layout = MainLayout.class)
@AnonymousAllowed
public class RaceWeekendDetailView extends BaseView {
    private final RaceWeekendService raceWeekendService;
    private final StewardingIncidentService incidentService;
    private final StewardingTrackService trackService;
    private final PenaltyCatalogService catalogService;
    private final SecurityService securityService;

    public RaceWeekendDetailView(RaceWeekendService raceWeekendService, StewardingIncidentService incidentService,
                                 StewardingTrackService trackService, PenaltyCatalogService catalogService,
                                 SecurityService securityService) {
        this.raceWeekendService = raceWeekendService;
        this.incidentService = incidentService;
        this.trackService = trackService;
        this.catalogService = catalogService;
        this.securityService = securityService;
    }

    @Override
    public String getPageTitle() {
        return "Race Weekend Details";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        removeAll();

        String weekendIdParam = event.getRouteParameters().get("weekendId").orElse(null);
        if (weekendIdParam == null) {
            getUI().ifPresent(ui -> ui.navigate(RaceWeekendListView.class));
            return;
        }

        Integer weekendId;
        try {
            weekendId = Integer.valueOf(weekendIdParam);
        } catch (NumberFormatException e) {
            getUI().ifPresent(ui -> ui.navigate(RaceWeekendListView.class));
            return;
        }

        RaceWeekend weekend = raceWeekendService.getWeekendById(weekendId);
        if (weekend == null) {
            getUI().ifPresent(ui -> ui.navigate(RaceWeekendListView.class));
            return;
        }

        add(createViewHeader(weekend.getTitle()));

        Button backButton = new Button("← Back to Race Weekends", e ->
                getUI().ifPresent(ui -> ui.navigate(RaceWeekendListView.class)));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        add(backButton);

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setPadding(true);
        infoLayout.setSpacing(false);
        if (weekend.getDescription() != null && !weekend.getDescription().isEmpty()) {
            infoLayout.add(new Span(weekend.getDescription()));
        }
        if (weekend.getTrack() != null) {
            infoLayout.add(createDetailRow("Track", weekend.getTrack().getName()));
        }
        if (weekend.getPenaltyCatalog() != null) {
            infoLayout.add(createDetailRow("Penalty Catalog", weekend.getPenaltyCatalog().getName()));
        }
        if (weekend.getStartDate() != null && weekend.getEndDate() != null) {
            infoLayout.add(createDetailRow("Date", weekend.getStartDate() + " — " + weekend.getEndDate()));
        }
        infoLayout.add(createDetailRow("Video URL Enabled", Boolean.TRUE.equals(weekend.getVideoUrlEnabled()) ? "Yes" : "No"));
        add(infoLayout);

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            HorizontalLayout weekendActionLayout = new HorizontalLayout();
            Button editWeekendButton = new Button("Edit Weekend");
            editWeekendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            editWeekendButton.addClickListener(e -> openEditWeekendDialog(weekend));
            weekendActionLayout.add(editWeekendButton);

            Button deleteWeekendButton = new Button("Delete Weekend");
            deleteWeekendButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteWeekendButton.addClickListener(e -> {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setHeader("Delete Weekend");
                confirmDialog.setText("Are you sure you want to delete this weekend?");
                confirmDialog.setCancelable(true);
                confirmDialog.setConfirmText("Delete");
                confirmDialog.setConfirmButtonTheme("error primary");
                confirmDialog.addConfirmListener(ev -> {
                    raceWeekendService.deleteWeekend(weekendId);
                    getUI().ifPresent(ui -> ui.navigate(RaceWeekendListView.class));
                });
                confirmDialog.open();
            });
            weekendActionLayout.add(deleteWeekendButton);
            add(weekendActionLayout);
        }

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        tabSheet.add("Sessions", createSessionsTab(weekendId));
        tabSheet.add("Incidents", createIncidentsTab(weekendId));

        addAndExpand(tabSheet);
    }

    private HorizontalLayout createDetailRow(String label, String value) {
        Span labelSpan = new Span(label + ": ");
        labelSpan.getStyle().set("font-weight", "bold");
        Span valueSpan = new Span(value);
        HorizontalLayout row = new HorizontalLayout(labelSpan, valueSpan);
        row.setSpacing(false);
        row.getStyle().set("gap", "var(--lumo-space-xs)");
        return row;
    }

    private VerticalLayout createSessionsTab(Integer weekendId) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            Button addSessionButton = new Button("Add Session");
            addSessionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            addSessionButton.addClickListener(e -> openAddSessionDialog(weekendId));
            layout.add(addSessionButton);
        }

        List<RaceWeekendSession> sessions = raceWeekendService.getSessionsByWeekendId(weekendId);
        Grid<RaceWeekendSession> grid = new Grid<>(RaceWeekendSession.class, false);
        grid.addColumn(session -> session.getSessionType() != null ? session.getSessionType().getDescription() : "-")
                .setHeader("Type").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(RaceWeekendSession::getTitle).setHeader("Title").setSortable(true);
        grid.addColumn(RaceWeekendSession::getStartTime).setHeader("Start Time").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(RaceWeekendSession::getEndTime).setHeader("End Time").setAutoWidth(true).setFlexGrow(0);
        grid.setItems(sessions);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(RaceWeekendSessionDetailView.class,
                        new RouteParameters(
                                new RouteParam("weekendId", String.valueOf(weekendId)),
                                new RouteParam("sessionId", String.valueOf(e.getItem().getId()))
                        )))
        );

        layout.addAndExpand(grid);
        return layout;
    }

    private void openAddSessionDialog(Integer weekendId) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add Session");
        dialog.setWidth("600px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        ComboBox<StewSessionType> typeCombo = new ComboBox<>("Session Type");
        typeCombo.setItems(StewSessionType.values());
        typeCombo.setItemLabelGenerator(StewSessionType::getDescription);
        typeCombo.setRequired(true);
        typeCombo.setWidthFull();

        TextField titleField = new TextField("Title");
        titleField.setRequired(true);
        titleField.setWidthFull();

        DateTimePicker startTimePicker = new DateTimePicker("Start Time");
        startTimePicker.setWidthFull();

        DateTimePicker endTimePicker = new DateTimePicker("End Time");
        endTimePicker.setWidthFull();

        IntegerField sortOrderField = new IntegerField("Sort Order");
        sortOrderField.setMin(0);
        sortOrderField.setValue(0);
        sortOrderField.setWidthFull();

        form.add(typeCombo, titleField, startTimePicker, endTimePicker, sortOrderField);

        Button saveButton = new Button("Save", e -> {
            if (titleField.isEmpty() || typeCombo.isEmpty()) {
                Notification.show("Session type and title are required", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            RaceWeekendSession session = RaceWeekendSession.builder()
                    .raceWeekendId(weekendId)
                    .sessionType(typeCombo.getValue())
                    .title(titleField.getValue())
                    .startTime(startTimePicker.getValue() != null ? startTimePicker.getValue().toInstant(ZoneOffset.UTC) : null)
                    .endTime(endTimePicker.getValue() != null ? endTimePicker.getValue().toInstant(ZoneOffset.UTC) : null)
                    .sortOrder(sortOrderField.getValue())
                    .build();

            raceWeekendService.createSession(session);
            dialog.close();
            Notification.show("Session created", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.add(form);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private VerticalLayout createIncidentsTab(Integer weekendId) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        List<RaceWeekendSession> sessions = raceWeekendService.getSessionsByWeekendId(weekendId);
        List<Incident> allIncidents = new ArrayList<>();
        for (RaceWeekendSession session : sessions) {
            allIncidents.addAll(incidentService.getIncidentsBySessionId(session.getId()));
        }

        Grid<Incident> grid = new Grid<>(Incident.class, false);
        grid.addColumn(incident -> {
            RaceWeekendSession session = raceWeekendService.getSessionById(incident.getSessionId());
            return session != null ? session.getTitle() : "-";
        }).setHeader("Session").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(Incident::getTitle).setHeader("Title").setSortable(true);
        grid.addColumn(incident -> incident.getStatus() != null ? incident.getStatus().getDescription() : "-")
                .setHeader("Status").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(Incident::getCreatedAt).setHeader("Created").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.setItems(allIncidents);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        layout.addAndExpand(grid);
        return layout;
    }

    private void openEditWeekendDialog(RaceWeekend weekend) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Race Weekend");
        dialog.setWidth("700px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        TextField titleField = new TextField("Title");
        titleField.setWidthFull();
        titleField.setRequired(true);
        titleField.setValue(weekend.getTitle() != null ? weekend.getTitle() : "");

        TextArea descriptionField = new TextArea("Description");
        descriptionField.setWidthFull();
        descriptionField.setValue(weekend.getDescription() != null ? weekend.getDescription() : "");

        ComboBox<StewardingTrack> trackCombo = new ComboBox<>("Track");
        List<StewardingTrack> tracks = trackService.getAllTracks();
        trackCombo.setItems(tracks);
        trackCombo.setItemLabelGenerator(StewardingTrack::getName);
        trackCombo.setWidthFull();
        if (weekend.getTrack() != null) {
            tracks.stream().filter(t -> t.getId().equals(weekend.getTrackId())).findFirst().ifPresent(trackCombo::setValue);
        }

        ComboBox<PenaltyCatalog> catalogCombo = new ComboBox<>("Penalty Catalog");
        List<PenaltyCatalog> catalogs = catalogService.getAllCatalogs();
        catalogCombo.setItems(catalogs);
        catalogCombo.setItemLabelGenerator(PenaltyCatalog::getName);
        catalogCombo.setWidthFull();
        if (weekend.getPenaltyCatalog() != null) {
            catalogs.stream().filter(c -> c.getId().equals(weekend.getPenaltyCatalogId())).findFirst().ifPresent(catalogCombo::setValue);
        }

        TextField webhookField = new TextField("Discord Webhook URL");
        webhookField.setWidthFull();
        webhookField.setValue(weekend.getDiscordWebhookUrl() != null ? weekend.getDiscordWebhookUrl() : "");

        Checkbox videoUrlEnabledCheckbox = new Checkbox("Enable Video URL for incident reports");
        videoUrlEnabledCheckbox.setValue(Boolean.TRUE.equals(weekend.getVideoUrlEnabled()));

        DatePicker startDatePicker = new DatePicker("Start Date");
        startDatePicker.setWidthFull();
        startDatePicker.setValue(weekend.getStartDate());

        DatePicker endDatePicker = new DatePicker("End Date");
        endDatePicker.setWidthFull();
        endDatePicker.setValue(weekend.getEndDate());

        form.add(titleField, 2);
        form.add(descriptionField, 2);
        form.add(trackCombo);
        form.add(catalogCombo);
        form.add(webhookField, 2);
        form.add(videoUrlEnabledCheckbox, 2);
        form.add(startDatePicker);
        form.add(endDatePicker);

        Button saveButton = new Button("Save", e -> {
            if (titleField.isEmpty()) {
                Notification.show("Title is required", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            weekend.setTitle(titleField.getValue());
            weekend.setDescription(descriptionField.getValue());
            weekend.setTrackId(trackCombo.getValue() != null ? trackCombo.getValue().getId() : null);
            weekend.setPenaltyCatalogId(catalogCombo.getValue() != null ? catalogCombo.getValue().getId() : null);
            weekend.setDiscordWebhookUrl(webhookField.getValue());
            weekend.setVideoUrlEnabled(videoUrlEnabledCheckbox.getValue());
            weekend.setStartDate(startDatePicker.getValue());
            weekend.setEndDate(endDatePicker.getValue());

            raceWeekendService.updateWeekend(weekend);
            dialog.close();
            Notification.show("Weekend updated", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.add(form);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }
}
