package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.stewarding.PenaltyCatalog;
import de.sustineo.simdesk.entities.stewarding.Round;
import de.sustineo.simdesk.entities.stewarding.Series;
import de.sustineo.simdesk.entities.stewarding.StewardingTrack;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.stewarding.PenaltyCatalogService;
import de.sustineo.simdesk.services.stewarding.RoundService;
import de.sustineo.simdesk.services.stewarding.SeriesService;
import de.sustineo.simdesk.services.stewarding.StewardingTrackService;
import de.sustineo.simdesk.views.BaseView;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/series/:seriesId", layout = MainLayout.class)
@AnonymousAllowed
public class SeriesDetailView extends BaseView {
    private final SeriesService seriesService;
    private final RoundService roundService;
    private final StewardingTrackService trackService;
    private final PenaltyCatalogService catalogService;
    private final SecurityService securityService;

    public SeriesDetailView(SeriesService seriesService, RoundService roundService,
                            StewardingTrackService trackService, PenaltyCatalogService catalogService,
                            SecurityService securityService) {
        this.seriesService = seriesService;
        this.roundService = roundService;
        this.trackService = trackService;
        this.catalogService = catalogService;
        this.securityService = securityService;
    }

    @Override
    public String getPageTitle() {
        return "Series Details";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        removeAll();

        String seriesIdParam = event.getRouteParameters().get("seriesId").orElse(null);
        if (seriesIdParam == null) {
            getUI().ifPresent(ui -> ui.navigate(SeriesListView.class));
            return;
        }

        String seriesId = seriesIdParam;

        Series series = seriesService.getSeriesById(seriesId);
        if (series == null) {
            getUI().ifPresent(ui -> ui.navigate(SeriesListView.class));
            return;
        }

        add(createViewHeader(series.getTitle()));

        Button backButton = new Button("← Back to Series", e ->
                getUI().ifPresent(ui -> ui.navigate(SeriesListView.class)));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        add(backButton);

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setPadding(true);
        infoLayout.setSpacing(false);
        if (series.getDescription() != null && !series.getDescription().isEmpty()) {
            infoLayout.add(new Span(series.getDescription()));
        }
        if (series.getPenaltyCatalog() != null) {
            infoLayout.add(createDetailRow("Penalty Catalog", series.getPenaltyCatalog().getName()));
        }
        if (series.getStartDate() != null && series.getEndDate() != null) {
            infoLayout.add(createDetailRow("Date", series.getStartDate() + " — " + series.getEndDate()));
        }
        infoLayout.add(createDetailRow("Video URL Enabled", Boolean.TRUE.equals(series.getVideoUrlEnabled()) ? "Yes" : "No"));
        add(infoLayout);

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            HorizontalLayout actionLayout = new HorizontalLayout();
            Button editButton = new Button("Edit Series");
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            editButton.addClickListener(e -> openEditSeriesDialog(series));
            actionLayout.add(editButton);

            Button deleteButton = new Button("Delete Series");
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setHeader("Delete Series");
                confirmDialog.setText("Are you sure you want to delete this series?");
                confirmDialog.setCancelable(true);
                confirmDialog.setConfirmText("Delete");
                confirmDialog.setConfirmButtonTheme("error primary");
                confirmDialog.addConfirmListener(ev -> {
                    seriesService.deleteSeries(seriesId);
                    getUI().ifPresent(ui -> ui.navigate(SeriesListView.class));
                });
                confirmDialog.open();
            });
            actionLayout.add(deleteButton);
            add(actionLayout);
        }

        // Rounds grid
        HorizontalLayout roundsHeader = new HorizontalLayout();
        roundsHeader.setWidthFull();
        roundsHeader.setAlignItems(Alignment.CENTER);

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            Button newRoundButton = new Button("New Round", e -> openNewRoundDialog(seriesId));
            newRoundButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            roundsHeader.add(newRoundButton);
        }

        add(roundsHeader);

        List<Round> rounds = roundService.getRoundsBySeriesId(seriesId);
        Grid<Round> grid = new Grid<>(Round.class, false);
        grid.addColumn(Round::getTitle).setHeader("Title").setSortable(true);
        grid.addColumn(round -> round.getTrack() != null ? round.getTrack().getName() : "-")
                .setHeader("Track").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(Round::getStartDate).setHeader("Start Date").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(Round::getEndDate).setHeader("End Date").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.setItems(rounds);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(RoundDetailView.class,
                        new RouteParameters(
                                new RouteParam("seriesId", String.valueOf(seriesId)),
                                new RouteParam("roundId", String.valueOf(e.getItem().getId()))
                        )))
        );

        addAndExpand(grid);
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

    private void openEditSeriesDialog(Series series) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Series");
        dialog.setWidth("700px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        TextField titleField = new TextField("Title");
        titleField.setWidthFull();
        titleField.setRequired(true);
        titleField.setValue(series.getTitle() != null ? series.getTitle() : "");

        TextArea descriptionField = new TextArea("Description");
        descriptionField.setWidthFull();
        descriptionField.setValue(series.getDescription() != null ? series.getDescription() : "");

        ComboBox<PenaltyCatalog> catalogCombo = new ComboBox<>("Penalty Catalog");
        List<PenaltyCatalog> catalogs = catalogService.getAllCatalogs();
        catalogCombo.setItems(catalogs);
        catalogCombo.setItemLabelGenerator(PenaltyCatalog::getName);
        catalogCombo.setWidthFull();
        if (series.getPenaltyCatalog() != null) {
            catalogs.stream().filter(c -> c.getId().equals(series.getPenaltyCatalogId())).findFirst().ifPresent(catalogCombo::setValue);
        }

        TextField webhookField = new TextField("Discord Webhook URL");
        webhookField.setWidthFull();
        webhookField.setValue(series.getDiscordWebhookUrl() != null ? series.getDiscordWebhookUrl() : "");

        Checkbox videoUrlEnabledCheckbox = new Checkbox("Enable Video URL for incident reports");
        videoUrlEnabledCheckbox.setValue(Boolean.TRUE.equals(series.getVideoUrlEnabled()));

        DatePicker startDatePicker = new DatePicker("Start Date");
        startDatePicker.setWidthFull();
        startDatePicker.setValue(series.getStartDate());

        DatePicker endDatePicker = new DatePicker("End Date");
        endDatePicker.setWidthFull();
        endDatePicker.setValue(series.getEndDate());

        form.add(titleField, 2);
        form.add(descriptionField, 2);
        form.add(catalogCombo, 2);
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

            series.setTitle(titleField.getValue());
            series.setDescription(descriptionField.getValue());
            series.setPenaltyCatalogId(catalogCombo.getValue() != null ? catalogCombo.getValue().getId() : null);
            series.setDiscordWebhookUrl(webhookField.getValue());
            series.setVideoUrlEnabled(videoUrlEnabledCheckbox.getValue());
            series.setStartDate(startDatePicker.getValue());
            series.setEndDate(endDatePicker.getValue());

            seriesService.updateSeries(series);
            dialog.close();
            Notification.show("Series updated", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.add(form);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void openNewRoundDialog(String seriesId) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("New Round");
        dialog.setWidth("600px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        TextField titleField = new TextField("Title");
        titleField.setWidthFull();
        titleField.setRequired(true);

        ComboBox<StewardingTrack> trackCombo = new ComboBox<>("Track");
        trackCombo.setItems(trackService.getAllTracks());
        trackCombo.setItemLabelGenerator(StewardingTrack::getName);
        trackCombo.setWidthFull();

        DatePicker startDatePicker = new DatePicker("Start Date");
        startDatePicker.setWidthFull();

        DatePicker endDatePicker = new DatePicker("End Date");
        endDatePicker.setWidthFull();

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

        Round round = Round.builder()
                    .seriesId(seriesId)
                    .title(titleField.getValue())
                    .trackId(trackCombo.getValue() != null ? trackCombo.getValue().getId() : null)
                    .startDate(startDatePicker.getValue())
                    .endDate(endDatePicker.getValue())
                    .build();

            roundService.createRound(round);
            dialog.close();
            Notification.show("Round created", 3000, Notification.Position.MIDDLE)
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
