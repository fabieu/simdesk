package de.sustineo.simdesk.views.stewarding;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.stewarding.Incident;
import de.sustineo.simdesk.entities.stewarding.RaceWeekend;
import de.sustineo.simdesk.entities.stewarding.RaceWeekendSession;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.stewarding.RaceWeekendService;
import de.sustineo.simdesk.services.stewarding.StewardingIncidentService;
import de.sustineo.simdesk.views.BaseView;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Route(value = "/stewarding/weekends/:weekendId/sessions/:sessionId", layout = MainLayout.class)
@AnonymousAllowed
public class RaceWeekendSessionDetailView extends BaseView {
    private final RaceWeekendService raceWeekendService;
    private final StewardingIncidentService incidentService;
    private final SecurityService securityService;

    public RaceWeekendSessionDetailView(RaceWeekendService raceWeekendService, StewardingIncidentService incidentService,
                                        SecurityService securityService) {
        this.raceWeekendService = raceWeekendService;
        this.incidentService = incidentService;
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

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setPadding(true);
        infoLayout.setSpacing(false);
        if (session.getSessionType() != null) {
            infoLayout.add(new Paragraph("Type: " + session.getSessionType().getDescription()));
        }
        if (session.getStartTime() != null) {
            infoLayout.add(new Paragraph("Start: " + session.getStartTime()));
        }
        if (session.getEndTime() != null) {
            infoLayout.add(new Paragraph("End: " + session.getEndTime()));
        }
        add(infoLayout);

        if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_STEWARD)) {
            HorizontalLayout actionLayout = new HorizontalLayout();
            Button reportIncidentButton = new Button("Report Incident");
            reportIncidentButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Button quickDecisionButton = new Button("Quick Decision");
            quickDecisionButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            actionLayout.add(reportIncidentButton, quickDecisionButton);
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

        addAndExpand(grid);
    }
}
