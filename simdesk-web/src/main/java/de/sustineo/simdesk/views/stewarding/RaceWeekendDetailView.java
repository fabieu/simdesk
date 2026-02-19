package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.UploadHandler;
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
import de.sustineo.simdesk.services.stewarding.RaceWeekendService;
import de.sustineo.simdesk.services.stewarding.StewardingEntrylistService;
import de.sustineo.simdesk.services.stewarding.StewardingIncidentService;
import de.sustineo.simdesk.views.BaseView;
import org.springframework.context.annotation.Profile;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/weekends/:weekendId", layout = MainLayout.class)
@AnonymousAllowed
public class RaceWeekendDetailView extends BaseView {
    private final RaceWeekendService raceWeekendService;
    private final StewardingIncidentService incidentService;
    private final StewardingEntrylistService entrylistService;
    private final SecurityService securityService;

    public RaceWeekendDetailView(RaceWeekendService raceWeekendService, StewardingIncidentService incidentService,
                                 StewardingEntrylistService entrylistService, SecurityService securityService) {
        this.raceWeekendService = raceWeekendService;
        this.incidentService = incidentService;
        this.entrylistService = entrylistService;
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
        add(infoLayout);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        tabSheet.add("Sessions", createSessionsTab(weekendId));
        tabSheet.add("Incidents", createIncidentsTab(weekendId));
        tabSheet.add("Entrylist", createEntrylistTab(weekendId));

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

    private VerticalLayout createEntrylistTab(Integer weekendId) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            Upload upload = new Upload();
            upload.setUploadHandler(UploadHandler.inMemory((metadata, data) -> {
                String json = new String(data, StandardCharsets.UTF_8);
                try {
                    entrylistService.uploadEntrylist(weekendId, json);
                    getUI().ifPresent(ui -> ui.access(() -> {
                        Notification.show("Entrylist uploaded successfully", 3000, Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        ui.getPage().reload();
                    }));
                } catch (IllegalArgumentException ex) {
                    getUI().ifPresent(ui -> ui.access(() ->
                            Notification.show("Invalid entrylist JSON: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR)
                    ));
                }
            }));
            upload.setAcceptedFileTypes("application/json", ".json");
            upload.setMaxFiles(1);
            layout.add(upload);
        }

        StewardingEntrylist entrylist = entrylistService.getEntrylistByWeekendId(weekendId);
        if (entrylist != null) {
            List<StewardingEntrylistEntry> entries = entrylistService.getEntriesByEntrylistId(entrylist.getId());
            Grid<StewardingEntrylistEntry> grid = new Grid<>(StewardingEntrylistEntry.class, false);
            grid.addColumn(StewardingEntrylistEntry::getRaceNumber).setHeader("Race Number").setAutoWidth(true).setFlexGrow(0).setSortable(true);
            grid.addColumn(StewardingEntrylistEntry::getTeamName).setHeader("Team Name").setSortable(true);
            grid.addColumn(StewardingEntrylistEntry::getDisplayName).setHeader("Display Name").setSortable(true);
            grid.setItems(entries);
            grid.setSizeFull();
            grid.setSelectionMode(Grid.SelectionMode.NONE);
            grid.setColumnReorderingAllowed(true);
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            layout.addAndExpand(grid);
        } else {
            layout.add(new H3("No entrylist uploaded yet"));
        }

        return layout;
    }
}
