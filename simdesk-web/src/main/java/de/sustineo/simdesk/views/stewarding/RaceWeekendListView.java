package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.stewarding.PenaltyCatalog;
import de.sustineo.simdesk.entities.stewarding.RaceWeekend;
import de.sustineo.simdesk.entities.stewarding.StewardingTrack;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.stewarding.PenaltyCatalogService;
import de.sustineo.simdesk.services.stewarding.RaceWeekendService;
import de.sustineo.simdesk.services.stewarding.StewardingTrackService;
import de.sustineo.simdesk.views.BaseView;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/weekends", layout = MainLayout.class)
@AnonymousAllowed
public class RaceWeekendListView extends BaseView {
    private final RaceWeekendService raceWeekendService;
    private final StewardingTrackService trackService;
    private final PenaltyCatalogService catalogService;
    private final SecurityService securityService;

    public RaceWeekendListView(RaceWeekendService raceWeekendService, StewardingTrackService trackService,
                               PenaltyCatalogService catalogService, SecurityService securityService) {
        this.raceWeekendService = raceWeekendService;
        this.trackService = trackService;
        this.catalogService = catalogService;
        this.securityService = securityService;
    }

    @Override
    public String getPageTitle() {
        return "Race Weekends";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        removeAll();

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.add(createViewHeader());

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            Button newButton = new Button("New Weekend", e -> openNewWeekendDialog());
            newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            headerLayout.add(newButton);
        }

        add(headerLayout);

        List<RaceWeekend> weekends = raceWeekendService.getAllWeekends();
        Grid<RaceWeekend> grid = new Grid<>(RaceWeekend.class, false);
        grid.addColumn(RaceWeekend::getTitle).setHeader("Title").setSortable(true);
        grid.addColumn(weekend -> {
            var track = trackService.getTrackById(weekend.getTrackId());
            return track != null ? track.getName() : "-";
        }).setHeader("Track").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(RaceWeekend::getStartDate).setHeader("Start Date").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(RaceWeekend::getEndDate).setHeader("End Date").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.setItems(weekends);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(RaceWeekendDetailView.class,
                        new RouteParameters("weekendId", String.valueOf(e.getItem().getId()))))
        );

        addAndExpand(grid);
    }

    private void openNewWeekendDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("New Race Weekend");
        dialog.setWidth("700px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        TextField titleField = new TextField("Title");
        titleField.setWidthFull();
        titleField.setRequired(true);

        TextArea descriptionField = new TextArea("Description");
        descriptionField.setWidthFull();

        ComboBox<StewardingTrack> trackCombo = new ComboBox<>("Track");
        trackCombo.setItems(trackService.getAllTracks());
        trackCombo.setItemLabelGenerator(StewardingTrack::getName);
        trackCombo.setWidthFull();

        ComboBox<PenaltyCatalog> catalogCombo = new ComboBox<>("Penalty Catalog");
        catalogCombo.setItems(catalogService.getAllCatalogs());
        catalogCombo.setItemLabelGenerator(PenaltyCatalog::getName);
        catalogCombo.setWidthFull();

        TextField webhookField = new TextField("Discord Webhook URL");
        webhookField.setWidthFull();

        DatePicker startDatePicker = new DatePicker("Start Date");
        startDatePicker.setWidthFull();

        DatePicker endDatePicker = new DatePicker("End Date");
        endDatePicker.setWidthFull();

        form.add(titleField, 2);
        form.add(descriptionField, 2);
        form.add(trackCombo);
        form.add(catalogCombo);
        form.add(webhookField, 2);
        Checkbox videoUrlEnabledCheckbox = new Checkbox("Enable Video URL for incident reports");
        form.add(videoUrlEnabledCheckbox, 2);
        form.add(startDatePicker);
        form.add(endDatePicker);

        Button saveButton = new Button("Save", e -> {
            if (titleField.isEmpty()) {
                Notification.show("Title is required", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            RaceWeekend weekend = RaceWeekend.builder()
                    .title(titleField.getValue())
                    .description(descriptionField.getValue())
                    .trackId(trackCombo.getValue() != null ? trackCombo.getValue().getId() : null)
                    .penaltyCatalogId(catalogCombo.getValue() != null ? catalogCombo.getValue().getId() : null)
                    .discordWebhookUrl(webhookField.getValue())
                    .videoUrlEnabled(videoUrlEnabledCheckbox.getValue())
                    .startDate(startDatePicker.getValue())
                    .endDate(endDatePicker.getValue())
                    .build();

            raceWeekendService.createWeekend(weekend);
            dialog.close();
            Notification.show("Race weekend created", 3000, Notification.Position.MIDDLE)
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
