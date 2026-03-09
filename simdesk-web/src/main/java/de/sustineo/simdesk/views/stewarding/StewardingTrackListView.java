package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.StewardingTrack;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.NotificationService;
import de.sustineo.simdesk.services.stewarding.StewardingTrackService;
import de.sustineo.simdesk.views.BaseView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/tracks", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "STEWARD"})
public class StewardingTrackListView extends BaseView {
    private final StewardingTrackService trackService;
    private final NotificationService notificationService;
    private Grid<StewardingTrack> grid;

    public StewardingTrackListView(StewardingTrackService trackService, NotificationService notificationService) {
        this.trackService = trackService;
        this.notificationService = notificationService;
    }

    @Override
    public String getPageTitle() {
        return "Stewarding Tracks";
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

        Button newButton = new Button("New Track", e -> openNewTrackDialog());
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        headerLayout.add(newButton);

        add(headerLayout);

        List<StewardingTrack> tracks = trackService.getAllTracks();
        grid = new Grid<>(StewardingTrack.class, false);
        grid.addColumn(StewardingTrack::getName).setHeader("Name").setSortable(true);
        grid.addColumn(StewardingTrack::getCountry).setHeader("Country").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.setItems(tracks);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        addAndExpand(grid);
    }

    private void openNewTrackDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("New Track");
        dialog.setWidth("600px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        TextField nameField = new TextField("Name");
        nameField.setRequired(true);
        nameField.setWidthFull();

        TextField countryField = new TextField("Country");
        countryField.setWidthFull();

        TextField mapImageUrlField = new TextField("Map Image URL");
        mapImageUrlField.setWidthFull();

        form.add(nameField, countryField, mapImageUrlField);

        Button saveButton = new Button("Save", e -> {
            if (nameField.isEmpty()) {
                notificationService.showErrorNotification("Name is required");
                return;
            }

            StewardingTrack track = StewardingTrack.builder()
                    .name(nameField.getValue())
                    .country(countryField.getValue())
                    .mapImageUrl(mapImageUrlField.getValue())
                    .build();

            trackService.createTrack(track);
            dialog.close();
            notificationService.showSuccessNotification("Track created");
            grid.setItems(trackService.getAllTracks());
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.add(form);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }
}
