package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.stewarding.RaceWeekend;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.auth.SecurityService;
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
    private final SecurityService securityService;

    public RaceWeekendListView(RaceWeekendService raceWeekendService, StewardingTrackService trackService, SecurityService securityService) {
        this.raceWeekendService = raceWeekendService;
        this.trackService = trackService;
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

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN)) {
            Button newButton = new Button("New Weekend", e ->
                    getUI().ifPresent(ui -> ui.navigate(RaceWeekendFormView.class)));
            newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            headerLayout.add(newButton);
        }

        add(headerLayout);

        List<RaceWeekend> weekends = raceWeekendService.getAllWeekends();
        Grid<RaceWeekend> grid = new Grid<>(RaceWeekend.class, false);
        grid.addColumn(RaceWeekend::getTitle).setHeader("Title").setAutoWidth(true);
        grid.addColumn(weekend -> {
            var track = trackService.getTrackById(weekend.getTrackId());
            return track != null ? track.getName() : "-";
        }).setHeader("Track").setAutoWidth(true);
        grid.addColumn(RaceWeekend::getStartDate).setHeader("Start Date").setAutoWidth(true);
        grid.addColumn(RaceWeekend::getEndDate).setHeader("End Date").setAutoWidth(true);
        grid.setItems(weekends);
        grid.setSizeFull();
        grid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(RaceWeekendDetailView.class,
                        new RouteParameters("weekendId", String.valueOf(e.getItem().getId()))))
        );

        addAndExpand(grid);
    }
}
