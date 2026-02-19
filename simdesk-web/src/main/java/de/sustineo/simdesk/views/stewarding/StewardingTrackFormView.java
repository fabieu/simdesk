package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.StewardingTrack;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.stewarding.StewardingTrackService;
import de.sustineo.simdesk.views.BaseView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.context.annotation.Profile;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/tracks/new", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class StewardingTrackFormView extends BaseView {
    private final StewardingTrackService trackService;

    public StewardingTrackFormView(StewardingTrackService trackService) {
        this.trackService = trackService;
    }

    @Override
    public String getPageTitle() {
        return "New Track";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        removeAll();

        add(createViewHeader());

        TextField nameField = new TextField("Name");
        nameField.setWidthFull();
        nameField.setRequired(true);

        TextField countryField = new TextField("Country");
        countryField.setWidthFull();

        TextField mapImageUrlField = new TextField("Map Image URL");
        mapImageUrlField.setWidthFull();

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );
        formLayout.add(nameField);
        formLayout.add(countryField);
        formLayout.add(mapImageUrlField, 2);

        Button saveButton = new Button("Save", e -> {
            if (nameField.isEmpty()) {
                Notification.show("Name is required", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            StewardingTrack track = StewardingTrack.builder()
                    .name(nameField.getValue())
                    .country(countryField.getValue())
                    .mapImageUrl(mapImageUrlField.getValue())
                    .build();

            trackService.createTrack(track);
            Notification.show("Track created", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.navigate(StewardingTrackListView.class));
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e ->
                getUI().ifPresent(ui -> ui.navigate(StewardingTrackListView.class)));

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);

        add(formLayout, buttonLayout);
    }
}
