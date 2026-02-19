package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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
import de.sustineo.simdesk.services.stewarding.RoundService;
import de.sustineo.simdesk.services.stewarding.SeriesService;
import de.sustineo.simdesk.services.stewarding.StewardingEntrylistService;
import de.sustineo.simdesk.services.stewarding.StewardingIncidentService;
import de.sustineo.simdesk.services.stewarding.StewardingTrackService;
import de.sustineo.simdesk.views.BaseView;
import org.springframework.context.annotation.Profile;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/series/:seriesId/rounds/:roundId", layout = MainLayout.class)
@AnonymousAllowed
public class RoundDetailView extends BaseView {
    private final SeriesService seriesService;
    private final RoundService roundService;
    private final StewardingIncidentService incidentService;
    private final StewardingEntrylistService entrylistService;
    private final StewardingTrackService trackService;
    private final SecurityService securityService;

    public RoundDetailView(SeriesService seriesService, RoundService roundService,
                           StewardingIncidentService incidentService, StewardingEntrylistService entrylistService,
                           StewardingTrackService trackService, SecurityService securityService) {
        this.seriesService = seriesService;
        this.roundService = roundService;
        this.incidentService = incidentService;
        this.entrylistService = entrylistService;
        this.trackService = trackService;
        this.securityService = securityService;
    }

    @Override
    public String getPageTitle() {
        return "Round Details";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        removeAll();

        String seriesIdParam = event.getRouteParameters().get("seriesId").orElse(null);
        String roundIdParam = event.getRouteParameters().get("roundId").orElse(null);
        if (seriesIdParam == null || roundIdParam == null) {
            getUI().ifPresent(ui -> ui.navigate(SeriesListView.class));
            return;
        }

        Integer seriesId;
        Integer roundId;
        try {
            seriesId = Integer.valueOf(seriesIdParam);
            roundId = Integer.valueOf(roundIdParam);
        } catch (NumberFormatException e) {
            getUI().ifPresent(ui -> ui.navigate(SeriesListView.class));
            return;
        }

        Series series = seriesService.getSeriesById(seriesId);
        Round round = roundService.getRoundById(roundId);
        if (series == null || round == null) {
            getUI().ifPresent(ui -> ui.navigate(SeriesListView.class));
            return;
        }

        add(createViewHeader(round.getTitle()));

        Button backButton = new Button("← Back to " + series.getTitle(), e ->
                getUI().ifPresent(ui -> ui.navigate(SeriesDetailView.class,
                        new RouteParameters("seriesId", String.valueOf(seriesId)))));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        add(backButton);

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setPadding(true);
        infoLayout.setSpacing(false);
        if (round.getTrack() != null) {
            infoLayout.add(createDetailRow("Track", round.getTrack().getName()));
        }
        if (round.getStartDate() != null && round.getEndDate() != null) {
            infoLayout.add(createDetailRow("Date", round.getStartDate() + " — " + round.getEndDate()));
        }
        add(infoLayout);

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            HorizontalLayout actionLayout = new HorizontalLayout();
            Button editButton = new Button("Edit Round");
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            editButton.addClickListener(e -> openEditRoundDialog(round, seriesId));
            actionLayout.add(editButton);

            Button deleteButton = new Button("Delete Round");
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setHeader("Delete Round");
                confirmDialog.setText("Are you sure you want to delete this round?");
                confirmDialog.setCancelable(true);
                confirmDialog.setConfirmText("Delete");
                confirmDialog.setConfirmButtonTheme("error primary");
                confirmDialog.addConfirmListener(ev -> {
                    roundService.deleteRound(roundId);
                    getUI().ifPresent(ui -> ui.navigate(SeriesDetailView.class,
                            new RouteParameters("seriesId", String.valueOf(seriesId))));
                });
                confirmDialog.open();
            });
            actionLayout.add(deleteButton);
            add(actionLayout);
        }

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        tabSheet.add("Sessions", createSessionsTab(roundId));
        tabSheet.add("Incidents", createIncidentsTab(seriesId, roundId, series));
        tabSheet.add("Entrylist", createEntrylistTab(roundId));

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

    private VerticalLayout createSessionsTab(Integer roundId) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            Button addSessionButton = new Button("Add Session");
            addSessionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            addSessionButton.addClickListener(e -> openAddSessionDialog(roundId));
            layout.add(addSessionButton);
        }

        List<RoundSession> sessions = roundService.getSessionsByRoundId(roundId);
        Grid<RoundSession> grid = new Grid<>(RoundSession.class, false);
        grid.addColumn(session -> session.getSessionType() != null ? session.getSessionType().getDescription() : "-")
                .setHeader("Type").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(RoundSession::getTitle).setHeader("Title").setSortable(true);
        grid.addColumn(RoundSession::getStartTime).setHeader("Start Time").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(RoundSession::getEndTime).setHeader("End Time").setAutoWidth(true).setFlexGrow(0);
        grid.setItems(sessions);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        layout.addAndExpand(grid);
        return layout;
    }

    private void openAddSessionDialog(Integer roundId) {
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

            RoundSession session = RoundSession.builder()
                    .roundId(roundId)
                    .sessionType(typeCombo.getValue())
                    .title(titleField.getValue())
                    .startTime(startTimePicker.getValue() != null ? startTimePicker.getValue().toInstant(ZoneOffset.UTC) : null)
                    .endTime(endTimePicker.getValue() != null ? endTimePicker.getValue().toInstant(ZoneOffset.UTC) : null)
                    .sortOrder(sortOrderField.getValue())
                    .build();

            roundService.createSession(session);
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

    private VerticalLayout createIncidentsTab(Integer seriesId, Integer roundId, Series series) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD, UserRoleEnum.ROLE_DRIVER)) {
            Button reportButton = new Button("Report Incident");
            reportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            reportButton.addClickListener(e -> openReportIncidentDialog(seriesId, roundId, series));
            layout.add(reportButton);
        }

        List<RoundSession> sessions = roundService.getSessionsByRoundId(roundId);
        List<Incident> allIncidents = new ArrayList<>();
        for (RoundSession session : sessions) {
            allIncidents.addAll(incidentService.getIncidentsBySessionId(session.getId()));
        }

        Grid<Incident> grid = new Grid<>(Incident.class, false);
        grid.addColumn(incident -> {
            RoundSession session = roundService.getSessionById(incident.getSessionId());
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
        grid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(IncidentDetailView.class,
                        new RouteParameters(
                                new RouteParam("seriesId", String.valueOf(seriesId)),
                                new RouteParam("roundId", String.valueOf(roundId)),
                                new RouteParam("incidentId", String.valueOf(e.getItem().getId()))
                        )))
        );

        layout.addAndExpand(grid);
        return layout;
    }

    private void openReportIncidentDialog(Integer seriesId, Integer roundId, Series series) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Report Incident");
        dialog.setWidth("600px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        List<RoundSession> sessions = roundService.getSessionsByRoundId(roundId);
        ComboBox<RoundSession> sessionCombo = new ComboBox<>("Session");
        sessionCombo.setItems(sessions);
        sessionCombo.setItemLabelGenerator(RoundSession::getTitle);
        sessionCombo.setRequired(true);
        sessionCombo.setWidthFull();

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

        MultiSelectComboBox<StewardingEntrylistEntry> involvedCarsCombo = new MultiSelectComboBox<>("Involved Cars");
        involvedCarsCombo.setWidthFull();
        StewardingEntrylist entrylist = entrylistService.getEntrylistByRoundId(roundId);
        if (entrylist != null) {
            List<StewardingEntrylistEntry> entries = entrylistService.getEntriesByEntrylistId(entrylist.getId());
            involvedCarsCombo.setItems(entries);
        }
        involvedCarsCombo.setItemLabelGenerator(StewardingEntrylistEntry::getDisplayName);

        form.add(sessionCombo, 2);
        form.add(titleField, 2);
        form.add(descriptionField, 2);
        form.add(mapMarkerXField, mapMarkerYField);
        form.add(involvedCarsCombo, 2);

        final TextField videoUrlField;
        if (Boolean.TRUE.equals(series.getVideoUrlEnabled())) {
            videoUrlField = new TextField("Video URL");
            videoUrlField.setWidthFull();
            form.add(videoUrlField, 2);
        } else {
            videoUrlField = null;
        }

        Button saveButton = new Button("Report", e -> {
            if (sessionCombo.isEmpty()) {
                Notification.show("Session is required", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
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
                    .sessionId(sessionCombo.getValue().getId())
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

    private VerticalLayout createEntrylistTab(Integer roundId) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(true);
        layout.add(new H3("Entrylist"));

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            Upload upload = new Upload();
            upload.setUploadHandler(UploadHandler.inMemory((metadata, data) -> {
                String json = new String(data, StandardCharsets.UTF_8);
                try {
                    entrylistService.uploadEntrylistForRound(roundId, json);
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

        StewardingEntrylist entrylist = entrylistService.getEntrylistByRoundId(roundId);
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
            layout.addAndExpand(entrylistGrid);
        }

        return layout;
    }

    private void openEditRoundDialog(Round round, Integer seriesId) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Round");
        dialog.setWidth("600px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        TextField titleField = new TextField("Title");
        titleField.setWidthFull();
        titleField.setRequired(true);
        titleField.setValue(round.getTitle() != null ? round.getTitle() : "");

        ComboBox<StewardingTrack> trackCombo = new ComboBox<>("Track");
        List<StewardingTrack> tracks = trackService.getAllTracks();
        trackCombo.setItems(tracks);
        trackCombo.setItemLabelGenerator(StewardingTrack::getName);
        trackCombo.setWidthFull();
        if (round.getTrack() != null) {
            tracks.stream().filter(t -> t.getId().equals(round.getTrackId())).findFirst().ifPresent(trackCombo::setValue);
        }

        DatePicker startDatePicker = new DatePicker("Start Date");
        startDatePicker.setWidthFull();
        startDatePicker.setValue(round.getStartDate());

        DatePicker endDatePicker = new DatePicker("End Date");
        endDatePicker.setWidthFull();
        endDatePicker.setValue(round.getEndDate());

        form.add(titleField, 2);
        form.add(trackCombo, 2);
        form.add(startDatePicker);
        form.add(endDatePicker);

        Button saveButton = new Button("Save", e -> {
            if (titleField.isEmpty()) {
                Notification.show("Title is required", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            round.setTitle(titleField.getValue());
            round.setTrackId(trackCombo.getValue() != null ? trackCombo.getValue().getId() : null);
            round.setStartDate(startDatePicker.getValue());
            round.setEndDate(endDatePicker.getValue());

            roundService.updateRound(round);
            dialog.close();
            Notification.show("Round updated", 3000, Notification.Position.MIDDLE)
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
