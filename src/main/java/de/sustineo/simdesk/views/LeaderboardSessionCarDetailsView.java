package de.sustineo.simdesk.views;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
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
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.*;
import de.sustineo.simdesk.entities.auth.UserRole;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.leaderboard.*;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.generators.InvalidLapPartNameGenerator;
import de.sustineo.simdesk.views.renderers.LapRenderer;
import de.sustineo.simdesk.views.renderers.SessionDetailsRenderer;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/sessions/:fileChecksum/details/:carId")
@PageTitle("Leaderboard - Session Car Details")
@AnonymousAllowed
public class LeaderboardSessionCarDetailsView extends BaseView implements BeforeEnterObserver {
    private final SessionService sessionService;
    private final LapService lapService;
    private final PenaltyService penaltyService;
    private final RankingService rankingService;
    private final LeaderboardService leaderboardService;
    private final SecurityService securityService;

    private List<Lap> laps = new ArrayList<>();
    private List<Penalty> penalties = new ArrayList<>();

    public LeaderboardSessionCarDetailsView(SessionService sessionService,
                                            LapService lapService,
                                            PenaltyService penaltyService,
                                            RankingService rankingService,
                                            LeaderboardService leaderboardService,
                                            SecurityService securityService) {
        this.sessionService = sessionService;
        this.lapService = lapService;
        this.penaltyService = penaltyService;
        this.rankingService = rankingService;
        this.leaderboardService = leaderboardService;
        this.securityService = securityService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        RouteParameters routeParameters = beforeEnterEvent.getRouteParameters();

        String fileChecksum = routeParameters.get(ROUTE_PARAMETER_FILE_CHECKSUM).orElseThrow();
        int carId = Integer.parseInt(routeParameters.get(ROUTE_PARAMETER_CAR_ID).orElseThrow());

        Session session = sessionService.getSessionByFileChecksum(fileChecksum);
        if (session == null) {
            throw new NotFoundException("Session with file checksum " + fileChecksum + " does not exist.");
        }

        List<String> playerIds = leaderboardService.getPlayerIdsBySessionAndCarId(session.getId(), carId);
        if (playerIds == null || playerIds.isEmpty()) {
            throw new NotFoundException("No car found in session with file checksum " + fileChecksum + " and car id " + carId);
        }

        laps = lapService.getLapsBySessionAndDrivers(session.getId(), playerIds);
        penalties = penaltyService.findBySessionAndCarId(session.getId(), carId).stream()
                .filter(Penalty::isValid)
                .collect(Collectors.toList());

        add(createViewHeader());
        add(createSessionInformation(session));

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED);
        tabSheet.add(createTab("Laps", laps.size()), createLapsGrid());
        tabSheet.add(createTab("Penalties", penalties.size()), createPenaltyGrid());
        if (securityService.hasAnyAuthority(UserRole.ADMIN)) {
            tabSheet.add(createTab("Statistics"), createStatisticsLayout(fileChecksum, carId));
        }

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        addAndExpand(tabSheet);
        add(createFooter());
    }

    private Component createSessionInformation(Session session) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(true);
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);

        H3 heading = new H3();
        heading.setText(String.format("%s - %s - %s", session.getSessionType().getDescription(), Track.getTrackNameByAccId(session.getTrackId()), session.getServerName()));

        Icon weatherIcon = getWeatherIcon(session);

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
                .setWidth(GRID_RANKING_WIDTH)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFrozen(true);
        grid.addColumn(lap -> Car.getGroupById(lap.getCarModelId()))
                .setHeader("Car Group")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(lap -> Car.getNameById(lap.getCarModelId()))
                .setHeader("Car Model")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Lap::getLapTimeMillis);
        grid.addColumn(lap -> lap.getDriver().getFullNameCensored())
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
                .setWidth(GRID_RANKING_WIDTH)
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

    private Component createStatisticsLayout(String fileChecksum, int carId) {
        VerticalLayout layout = new VerticalLayout();

        StreamResource csvResource = new StreamResource(
                String.format("session_laps_%s_%s.csv", fileChecksum, carId),
                () -> {
                    String csv = this.exportLapsCSV();
                    return new ByteArrayInputStream(csv != null ? csv.getBytes(StandardCharsets.UTF_8) : new byte[0]);
                }
        );

        Anchor downloadLapsAnchor = createDownloadAnchor(csvResource, "Download laps");

        layout.add(downloadLapsAnchor);
        return layout;
    }

    private String exportLapsCSV() {
        if (laps == null) {
            return null;
        }

        try (StringWriter writer = new StringWriter()) {
            StatefulBeanToCsv<Lap> sbc = new StatefulBeanToCsvBuilder<Lap>(writer)
                    .withSeparator(';')
                    .build();
            sbc.write(laps);

            return writer.toString();
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
            log.severe("An error occurred during creation of CSV resource: " + e.getMessage());
            return null;
        }
    }

    private Tab createTab(String label) {
        return createTab(label, null);
    }

    private Tab createTab(String label, Integer number) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(Alignment.CENTER);
        horizontalLayout.getStyle()
                .set("gap", "var(--lumo-space-s)");

        Span labelSpan = new Span(label);
        horizontalLayout.add(labelSpan);

        if (number != null) {
            Span numberSpan = new Span(String.valueOf(number));
            numberSpan.getElement().getThemeList().add("badge contrast");
            horizontalLayout.add(numberSpan);
        }

        return new Tab(horizontalLayout);
    }
}
