package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.configuration.VaadinConfiguration;
import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.Penalty;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.leaderboard.LapService;
import de.sustineo.simdesk.services.leaderboard.PenaltyService;
import de.sustineo.simdesk.services.leaderboard.RankingService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.generators.InvalidLapPartNameGenerator;
import de.sustineo.simdesk.views.renderers.LapRenderer;
import de.sustineo.simdesk.views.renderers.SessionDetailsRenderer;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final PenaltyService penaltyService;
    private final RankingService rankingService;
    private List<Lap> laps = new ArrayList<>();
    private List<Penalty> penalties = new ArrayList<>();

    public SessionDetailsView(SessionService sessionService,
                              LapService lapService,
                              PenaltyService penaltyService,
                              RankingService rankingService) {
        this.sessionService = sessionService;
        this.lapService = lapService;
        this.penaltyService = penaltyService;
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

    private Component createLapsGrid() {
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

    private Component createPenaltyGrid() {
        Grid<Penalty> grid = new Grid<>(Penalty.class, false);
        grid.addColumn(LitRenderer.of("${index + 1}"))
                .setHeader("#")
                .setWidth(ComponentUtils.GRID_RANKING_WIDTH)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFrozen(true);
        grid.addColumn(Penalty::getPenaltyAbbreviation)
                .setHeader("Penalty")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(Penalty::getReasonDescription)
                .setHeader("Reason")
                .setSortable(true);
        grid.addColumn(Penalty::getViolationLapCorrected)
                .setHeader("Violation in lap")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(Penalty::getClearedLapCorrected)
                .setHeader("Cleared in lap")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(SessionDetailsRenderer.createPenaltyServedRenderer())
                .setHeader("Cleared")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true);

        grid.setItems(penalties);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        return grid;
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters routeParameters = event.getRouteParameters();

        String fileChecksum = routeParameters.get(ROUTE_PARAMETER_FILE_CHECKSUM).orElseThrow();
        int carId = Integer.parseInt(routeParameters.get(ROUTE_PARAMETER_CAR_ID).orElseThrow());

        Session session = sessionService.getSession(fileChecksum);
        if (session == null) {
            throw new NotFoundException("Session with file checksum " + fileChecksum + " does not exist.");
        }

        List<String> playerIds = rankingService.getPlayerIdsBySessionAndCarId(session.getId(), carId);
        if (playerIds == null || playerIds.isEmpty()) {
            throw new NotFoundException("No car found in session with file checksum " + fileChecksum + " and car id " + carId);
        }

        laps = lapService.getLapsBySessionAndDrivers(session.getId(), playerIds);
        penalties = penaltyService.findBySessionAndCarId(session.getId(), carId).stream()
                .filter(Penalty::isValid)
                .collect(Collectors.toList());

        add(createSessionInformation(session));

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED);
        tabSheet.add(createTab("Laps", laps.size()), createLapsGrid());
        tabSheet.add(createTab("Penalties", penalties.size()), createPenaltyGrid());
        addAndExpand(tabSheet);
    }

    private Tab createTab(String label, int number) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(Alignment.CENTER);
        horizontalLayout.getStyle()
                .set("gap", "var(--lumo-space-s)");

        Span labelSpan = new Span(label);
        Span numberSpan = new Span(String.valueOf(number));
        numberSpan.getElement().getThemeList().add("badge contrast");

        horizontalLayout.add(labelSpan, numberSpan);
        return new Tab(horizontalLayout);
    }
}