package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.PenaltyCatalog;
import de.sustineo.simdesk.entities.stewarding.RaceWeekend;
import de.sustineo.simdesk.entities.stewarding.StewardingTrack;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.stewarding.PenaltyCatalogService;
import de.sustineo.simdesk.services.stewarding.RaceWeekendService;
import de.sustineo.simdesk.services.stewarding.StewardingTrackService;
import de.sustineo.simdesk.views.BaseView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.context.annotation.Profile;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/weekends/new", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class RaceWeekendFormView extends BaseView {
    private final RaceWeekendService raceWeekendService;
    private final StewardingTrackService trackService;
    private final PenaltyCatalogService catalogService;

    public RaceWeekendFormView(RaceWeekendService raceWeekendService, StewardingTrackService trackService, PenaltyCatalogService catalogService) {
        this.raceWeekendService = raceWeekendService;
        this.trackService = trackService;
        this.catalogService = catalogService;
    }

    @Override
    public String getPageTitle() {
        return "New Race Weekend";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        removeAll();

        add(createViewHeader());

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

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );
        formLayout.add(titleField, 2);
        formLayout.add(descriptionField, 2);
        formLayout.add(trackCombo);
        formLayout.add(catalogCombo);
        formLayout.add(webhookField, 2);
        formLayout.add(startDatePicker);
        formLayout.add(endDatePicker);

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
                    .startDate(startDatePicker.getValue())
                    .endDate(endDatePicker.getValue())
                    .build();

            raceWeekendService.createWeekend(weekend);
            Notification.show("Race weekend created", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.navigate(RaceWeekendListView.class));
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e ->
                getUI().ifPresent(ui -> ui.navigate(RaceWeekendListView.class)));

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);

        add(formLayout, buttonLayout);
    }
}
