package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/weekends/:weekendId/sessions/:sessionId", layout = MainLayout.class)
@AnonymousAllowed
public class RaceWeekendSessionDetailView extends BaseView {
    private final RaceWeekendService raceWeekendService;
    private final StewardingIncidentService incidentService;
    private final StewardingEntrylistService entrylistService;
    private final SecurityService securityService;

    public RaceWeekendSessionDetailView(RaceWeekendService raceWeekendService, StewardingIncidentService incidentService,
                                        StewardingEntrylistService entrylistService, SecurityService securityService) {
        this.raceWeekendService = raceWeekendService;
        this.incidentService = incidentService;
        this.entrylistService = entrylistService;
        this.securityService = securityService;
    }

    @Override
    public String getPageTitle() {
        return "Session Details";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        removeAll();

        String weekendIdParam = event.getRouteParameters().get("weekendId").orElse(null);
        String sessionIdParam = event.getRouteParameters().get("sessionId").orElse(null);
        if (weekendIdParam == null || sessionIdParam == null) {
            getUI().ifPresent(ui -> ui.navigate(RaceWeekendListView.class));
            return;
        }

        Integer weekendId;
        Integer sessionId;
        try {
            weekendId = Integer.valueOf(weekendIdParam);
            sessionId = Integer.valueOf(sessionIdParam);
        } catch (NumberFormatException e) {
            getUI().ifPresent(ui -> ui.navigate(RaceWeekendListView.class));
            return;
        }

        RaceWeekend weekend = raceWeekendService.getWeekendById(weekendId);
        RaceWeekendSession session = raceWeekendService.getSessionById(sessionId);
        if (weekend == null || session == null) {
            getUI().ifPresent(ui -> ui.navigate(RaceWeekendListView.class));
            return;
        }

        add(createViewHeader(session.getTitle()));

        Button backButton = new Button("← Back to " + weekend.getTitle(), e ->
                getUI().ifPresent(ui -> ui.navigate(RaceWeekendDetailView.class,
                        new RouteParameters("weekendId", String.valueOf(weekendId)))));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        add(backButton);

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setPadding(true);
        infoLayout.setSpacing(false);
        if (session.getSessionType() != null) {
            infoLayout.add(createDetailRow("Type", session.getSessionType().getDescription()));
        }
        if (session.getStartTime() != null) {
            infoLayout.add(createDetailRow("Start", session.getStartTime().toString()));
        }
        if (session.getEndTime() != null) {
            infoLayout.add(createDetailRow("End", session.getEndTime().toString()));
        }
        add(infoLayout);

        HorizontalLayout actionLayout = new HorizontalLayout();

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD, UserRoleEnum.ROLE_DRIVER)) {
            Button reportIncidentButton = new Button("Report Incident");
            reportIncidentButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            reportIncidentButton.addClickListener(e -> openReportIncidentDialog(sessionId, weekendId, weekend));
            actionLayout.add(reportIncidentButton);
        }

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            Button quickDecisionButton = new Button("Quick Decision");
            quickDecisionButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            actionLayout.add(quickDecisionButton);

            Button editSessionButton = new Button("Edit Session");
            editSessionButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            actionLayout.add(editSessionButton);

            Button deleteSessionButton = new Button("Delete Session");
            deleteSessionButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteSessionButton.addClickListener(e -> {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setHeader("Delete Session");
                confirmDialog.setText("Are you sure you want to delete this session?");
                confirmDialog.setCancelable(true);
                confirmDialog.setConfirmText("Delete");
                confirmDialog.setConfirmButtonTheme("error primary");
                confirmDialog.addConfirmListener(ev -> {
                    raceWeekendService.deleteSession(sessionId);
                    getUI().ifPresent(ui -> ui.navigate(RaceWeekendDetailView.class,
                            new RouteParameters("weekendId", String.valueOf(weekendId))));
                });
                confirmDialog.open();
            });
            actionLayout.add(deleteSessionButton);
        }

        if (actionLayout.getComponentCount() > 0) {
            add(actionLayout);
        }

        List<Incident> incidents = incidentService.getIncidentsBySessionId(sessionId);
        Grid<Incident> grid = new Grid<>(Incident.class, false);
        grid.addColumn(Incident::getTitle).setHeader("Title").setSortable(true);
        grid.addColumn(incident -> incident.getStatus() != null ? incident.getStatus().getDescription() : "-")
                .setHeader("Status").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(Incident::getInvolvedCarsText).setHeader("Involved Cars").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Incident::getCreatedAt).setHeader("Created").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.setItems(incidents);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(IncidentDetailView.class,
                        new RouteParameters(
                                new RouteParam("weekendId", String.valueOf(weekendId)),
                                new RouteParam("incidentId", String.valueOf(e.getItem().getId()))
                        )))
        );

        add(grid);

        // Entrylist section
        VerticalLayout entrylistLayout = new VerticalLayout();
        entrylistLayout.setPadding(true);
        entrylistLayout.add(new H3("Entrylist"));

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            Upload upload = new Upload();
            upload.setUploadHandler(UploadHandler.inMemory((metadata, data) -> {
                String json = new String(data, StandardCharsets.UTF_8);
                try {
                    entrylistService.uploadEntrylistForSession(sessionId, weekendId, json);
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
            entrylistLayout.add(upload);
        }

        StewardingEntrylist entrylist = entrylistService.getEntrylistBySessionId(sessionId);
        if (entrylist != null) {
            List<StewardingEntrylistEntry> entries = entrylistService.getEntriesByEntrylistId(entrylist.getId());
            Grid<StewardingEntrylistEntry> entrylistGrid = new Grid<>(StewardingEntrylistEntry.class, false);
            entrylistGrid.addColumn(StewardingEntrylistEntry::getRaceNumber).setHeader("Race Number").setAutoWidth(true).setFlexGrow(0).setSortable(true);
            entrylistGrid.addColumn(StewardingEntrylistEntry::getTeamName).setHeader("Team Name").setSortable(true);
            entrylistGrid.addColumn(StewardingEntrylistEntry::getDisplayName).setHeader("Display Name").setSortable(true);
            entrylistGrid.setItems(entries);
            entrylistGrid.setSelectionMode(Grid.SelectionMode.NONE);
            entrylistGrid.setColumnReorderingAllowed(true);
            entrylistGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            entrylistLayout.add(entrylistGrid);
        }

        addAndExpand(entrylistLayout);
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

    private void openReportIncidentDialog(Integer sessionId, Integer weekendId, RaceWeekend weekend) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Report Incident");
        dialog.setWidth("600px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        TextField titleField = new TextField("Title");
        titleField.setRequired(true);
        titleField.setWidthFull();

        TextArea descriptionField = new TextArea("Description");
        descriptionField.setWidthFull();
        descriptionField.setMinHeight("100px");

        NumberField mapMarkerXField = new NumberField("Map Marker X");
        mapMarkerXField.setWidthFull();

        NumberField mapMarkerYField = new NumberField("Map Marker Y");
        mapMarkerYField.setWidthFull();

        // Populate involved cars from session entrylist
        MultiSelectComboBox<StewardingEntrylistEntry> involvedCarsCombo = new MultiSelectComboBox<>("Involved Cars");
        involvedCarsCombo.setWidthFull();
        StewardingEntrylist entrylist = entrylistService.getEntrylistBySessionId(sessionId);
        if (entrylist != null) {
            List<StewardingEntrylistEntry> entries = entrylistService.getEntriesByEntrylistId(entrylist.getId());
            involvedCarsCombo.setItems(entries);
        }
        involvedCarsCombo.setItemLabelGenerator(StewardingEntrylistEntry::getDisplayName);

        form.add(titleField, 2);
        form.add(descriptionField, 2);
        form.add(mapMarkerXField, mapMarkerYField);
        form.add(involvedCarsCombo, 2);

        final TextField videoUrlField;
        if (Boolean.TRUE.equals(weekend.getVideoUrlEnabled())) {
            videoUrlField = new TextField("Video URL");
            videoUrlField.setWidthFull();
            form.add(videoUrlField, 2);
        } else {
            videoUrlField = null;
        }

        Button saveButton = new Button("Report", e -> {
            if (titleField.isEmpty()) {
                Notification.show("Title is required", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            Set<StewardingEntrylistEntry> selectedEntries = involvedCarsCombo.getValue();
            String involvedCarsText = selectedEntries.stream()
                    .map(StewardingEntrylistEntry::getDisplayName)
                    .collect(Collectors.joining(", "));
            List<Integer> involvedEntryIds = selectedEntries.stream()
                    .map(StewardingEntrylistEntry::getId)
                    .collect(Collectors.toList());

            String videoUrl = videoUrlField != null ? videoUrlField.getValue() : null;

            Incident incident = Incident.builder()
                    .sessionId(sessionId)
                    .title(titleField.getValue())
                    .description(descriptionField.getValue())
                    .mapMarkerX(mapMarkerXField.getValue())
                    .mapMarkerY(mapMarkerYField.getValue())
                    .involvedCarsText(involvedCarsText)
                    .videoUrl(videoUrl)
                    .status(IncidentStatus.REPORTED)
                    .build();

            incidentService.createIncident(incident, involvedEntryIds);
            dialog.close();
            Notification.show("Incident reported", 3000, Notification.Position.MIDDLE)
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
