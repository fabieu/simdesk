package de.sustineo.acc.servertools.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.configuration.VaadinConfiguration;
import de.sustineo.acc.servertools.entities.Lap;
import de.sustineo.acc.servertools.entities.Session;
import de.sustineo.acc.servertools.layouts.MainLayout;
import de.sustineo.acc.servertools.services.leaderboard.LapService;
import de.sustineo.acc.servertools.services.leaderboard.RankingService;
import de.sustineo.acc.servertools.services.leaderboard.SessionService;
import de.sustineo.acc.servertools.utils.FormatUtils;
import de.sustineo.acc.servertools.views.generators.InvalidLapPartNameGenerator;
import de.sustineo.acc.servertools.views.renderers.ranking.LapRenderer;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Log
@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/sessions/:fileChecksum/details/:carId", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_SHORT_PREFIX + "Leaderboard - Session")
@AnonymousAllowed
public class SessionDetailsView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE_PARAMETER_FILE_CHECKSUM = "fileChecksum";
    public static final String ROUTE_PARAMETER_CAR_ID = "carId";
    private final SessionService sessionService;
    private final LapService lapService;
    private final RankingService rankingService;

    public SessionDetailsView(SessionService sessionService, LapService lapService, RankingService rankingService) {
        this.sessionService = sessionService;
        this.lapService = lapService;
        this.rankingService = rankingService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    private Component createSessionInformation(Session session) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);

        H3 heading = new H3();
        heading.setText(String.format("%s - %s - %s", session.getSessionType().getDescription(), session.getTrackName(), session.getServerName()));

        Icon weatherIcon = ComponentUtils.createWeatherIcon(session);

        Span sessionDatetimeBadge = new Span();
        sessionDatetimeBadge.setText(FormatUtils.formatDatetime(session.getSessionDatetime()));
        sessionDatetimeBadge.getElement().getThemeList().add("badge contrast");

        layout.add(weatherIcon, heading, sessionDatetimeBadge);
        return layout;
    }

    private Component createLapsGrid(Session session, int carId) {
        List<String> playerIds = rankingService.getPlayerIdsBySessionAndCarId(session.getId(), carId);
        List<Lap> laps = lapService.getLapsBySessionAndDrivers(session.getId(), playerIds);

        Grid<Lap> grid = new Grid<>(Lap.class, false);
        grid.addColumn(LitRenderer.of("${index + 1}"))
                .setHeader("#")
                .setWidth(ComponentUtils.GRID_RANKING_WIDTH)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFrozen(true);
        grid.addColumn(Lap::getCarGroup)
                .setHeader("Car Group")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(Lap::getCarModelName)
                .setHeader("Car Model")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Lap::getLapTimeMillis);
        grid.addColumn(lap -> lap.getDriver().getFullName())
                .setHeader("Driver")
                .setSortable(true);
        grid.addColumn(LapRenderer.createLapTimeRenderer())
                .setHeader("Lap Time")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setPartNameGenerator(ranking -> "font-weight-bold")
                .setSortable(true)
                .setComparator(Lap::getLapTimeMillis);
        grid.addColumn(LapRenderer.createSplit1Renderer())
                .setHeader("Split 1")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Lap::getSplit1Millis);
        grid.addColumn(LapRenderer.createSplit2Renderer())
                .setHeader("Split 2")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Lap::getSplit2Millis);
        grid.addColumn(LapRenderer.createSplit3Renderer())
                .setHeader("Split 3")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Lap::getSplit3Millis);

        grid.setItems(laps);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setPartNameGenerator(new InvalidLapPartNameGenerator());

        return grid;
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters routeParameters = event.getRouteParameters();

        String fileChecksum = routeParameters.get(ROUTE_PARAMETER_FILE_CHECKSUM).orElseThrow();
        int carId = Integer.parseInt(routeParameters.get(ROUTE_PARAMETER_CAR_ID).orElseThrow());

        try {
            Session session = sessionService.getSession(fileChecksum);
            if (session == null) {
                throw new IllegalArgumentException("Session with file checksum " + fileChecksum + " does not exist.");
            }

            add(createSessionInformation(session));
            addAndExpand(createLapsGrid(session, carId));
        } catch (IllegalArgumentException e) {
            event.rerouteToError(NotFoundException.class);
        }
    }
}
