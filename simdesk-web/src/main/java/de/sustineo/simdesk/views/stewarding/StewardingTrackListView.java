package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.StewardingTrack;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.stewarding.StewardingTrackService;
import de.sustineo.simdesk.views.BaseView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/tracks", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class StewardingTrackListView extends BaseView {
    private final StewardingTrackService trackService;

    public StewardingTrackListView(StewardingTrackService trackService) {
        this.trackService = trackService;
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

        Button newButton = new Button("New Track", e ->
                getUI().ifPresent(ui -> ui.navigate(StewardingTrackFormView.class)));
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        headerLayout.add(newButton);

        add(headerLayout);

        List<StewardingTrack> tracks = trackService.getAllTracks();
        Grid<StewardingTrack> grid = new Grid<>(StewardingTrack.class, false);
        grid.addColumn(StewardingTrack::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(StewardingTrack::getCountry).setHeader("Country").setAutoWidth(true);
        grid.setItems(tracks);
        grid.setSizeFull();

        addAndExpand(grid);
    }
}
