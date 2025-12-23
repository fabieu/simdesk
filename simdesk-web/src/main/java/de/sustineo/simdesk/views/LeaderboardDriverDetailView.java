package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.*;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.record.LapByTrack;
import de.sustineo.simdesk.entities.record.LapsByAccCar;
import de.sustineo.simdesk.entities.record.LapsByTrack;
import de.sustineo.simdesk.services.leaderboard.DriverAliasService;
import de.sustineo.simdesk.services.leaderboard.DriverService;
import de.sustineo.simdesk.services.leaderboard.LapService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.components.SessionComponentFactory;
import de.sustineo.simdesk.views.filter.grid.GridFilter;
import de.sustineo.simdesk.views.filter.grid.SessionFilter;
import de.sustineo.simdesk.views.generators.LapsByCarCarGroupPartNameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import java.util.*;
import java.util.stream.Collectors;

@Log
@Profile(SpringProfile.LEADERBOARD)
@Route(value = "/leaderboard/drivers/:driverId")
@AnonymousAllowed
@RequiredArgsConstructor
public class LeaderboardDriverDetailView extends BaseView {
    private final DriverService driverService;
    private final DriverAliasService driverAliasService;
    private final LapService lapService;
    private final SessionService sessionService;
    private final SessionComponentFactory sessionComponentFactory;

    @Override
    public String getPageTitle() {
        return "Leaderboard - Driver";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        routeParameters = beforeEnterEvent.getRouteParameters();

        String driverId = routeParameters.get(ROUTE_PARAMETER_DRIVER_ID).orElseThrow();

        Driver driver = driverService.getDriverById(driverId);
        if (driver == null) {
            beforeEnterEvent.rerouteToError(NotFoundException.class);
            return;
        }

        setSizeFull();
        setSpacing(false);
        setPadding(false);

        removeAll();

        add(createViewHeader(driver.getFullName()));
        addAndExpand(createDriverLayout(driver));
    }

    private Component createDriverLayout(Driver driver) {
        List<Lap> laps = lapService.getByDriverId(driver.getId());
        List<Session> sessions = sessionService.getAllByDriverId(driver.getId());

        Map<Integer, Session> sessionByIdMap = sessions.stream()
                .collect(Collectors.toMap(Session::getId, session -> session));

        Map<Track, List<Lap>> lapsByTrackMap = laps.stream()
                .filter(lap -> lap.getSessionId() != null)
                .collect(Collectors.groupingBy(lap -> sessionByIdMap.get(lap.getSessionId()).getTrack()));

        Div layout = new Div();
        layout.addClassNames("container", "bg-light");

        layout.add(createBadgeLayout(driver, laps));
        layout.add(createAliasesLayout(driver));

        layout.add(createSessionsLayout(sessions));
        layout.add(createFastestLapsLayout(lapsByTrackMap));
        layout.add(createFavoriteCarsLayout(laps));
        layout.add(createFavoriteTrackLayout(lapsByTrackMap));

        return layout;
    }

    private Component createBadgeLayout(Driver driver, List<Lap> laps) {
        FlexLayout layout = new FlexLayout();
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("gap", "var(--lumo-space-s)");

        Span lastSeenBadge = new Span("Last seen: " + FormatUtils.formatDatetime(driver.getLastActivity()));
        lastSeenBadge.getElement().getThemeList().add("badge");

        long validLapCount = laps.stream().filter(Lap::isValid).count();
        Span validLapCountBadge = new Span("Valid laps: " + validLapCount);
        validLapCountBadge.getElement().getThemeList().add("badge success");

        long invalidLapCount = laps.stream().filter(lap -> !lap.isValid()).count();
        Span invalidLapCountBadge = new Span("Invalid laps: " + invalidLapCount);
        invalidLapCountBadge.getElement().getThemeList().add("badge error");

        layout.add(lastSeenBadge, validLapCountBadge, invalidLapCountBadge);
        return layout;
    }

    private Component createAliasesLayout(Driver driver) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        layout.add(new H3("Known Aliases"));

        if (driver.getVisibility() == Visibility.PRIVATE) {
            layout.add(new Span("This driver's aliases are private."));
        } else {
            FlexLayout aliasLayout = new FlexLayout();
            aliasLayout.setWidthFull();
            aliasLayout.setAlignItems(Alignment.CENTER);
            aliasLayout.getStyle()
                    .setFlexWrap(Style.FlexWrap.WRAP)
                    .set("gap", "var(--lumo-space-s)");

            List<DriverAlias> aliases = driverAliasService.getLatestAliasesByDriverId(driver.getId(), 10);
            aliases.forEach((alias) -> {
                Span aliasBadge = new Span(alias.getFullName());
                aliasBadge.getElement().getThemeList().add("badge");
                aliasLayout.add(aliasBadge);
            });

            layout.add(aliasLayout);
        }

        return layout;
    }

    private Component createSessionsLayout(List<Session> sessions) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.getStyle()
                .set("gap", "var(--lumo-space-s)");

        headerLayout.add(new H3("Sessions"));

        sessions.stream().collect(Collectors.groupingBy(
                        Session::getSessionType,
                        () -> new EnumMap<>(SessionType.class),
                        Collectors.toList())
                )
                .forEach((sessionType, sessionList) -> {
                    Span badge = new Span(sessionType + ": " + sessionList.size());
                    badge.getElement().getThemeList().add("badge contrast small");
                    headerLayout.add(badge);
                });

        Grid<Session> grid = new Grid<>(Session.class, false);
        grid.addComponentColumn(sessionComponentFactory::getWeatherIcon)
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(session -> FormatUtils.formatDatetime(session.getSessionDatetime()))
                .setHeader("Session Time")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Session::getSessionDatetime);
        Grid.Column<Session> sessionTypeColumn = grid.addColumn(Session::getSessionType)
                .setHeader("Session")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Session> serverNameColumn = grid.addColumn(Session::getServerName)
                .setHeader("Server Name")
                .setSortable(true)
                .setTooltipGenerator(Session::getServerName);
        Grid.Column<Session> trackColumn = grid.addColumn(Session::getTrack)
                .setHeader("Track")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);

        GridListDataView<Session> dataView = grid.setItems(sessions);
        grid.setMultiSort(true, true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        SessionFilter sessionFilter = new SessionFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(serverNameColumn).setComponent(GridFilter.createTextFieldHeader(sessionFilter::setServerName));
        headerRow.getCell(trackColumn).setComponent(GridFilter.createComboBoxHeader(sessionFilter::setTrack, Track::getAllOfAccSortedByName));
        headerRow.getCell(sessionTypeColumn).setComponent(GridFilter.createComboBoxHeader(sessionFilter::setSessionType, SessionType::getValid));

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        SingleSelect<Grid<Session>, Session> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
            Session selectedSession = e.getValue();

            if (selectedSession != null) {
                getUI().ifPresent(ui -> ui.navigate(LeaderboardSessionDetailView.class,
                        new RouteParameters(
                                new RouteParam(ROUTE_PARAMETER_FILE_CHECKSUM, selectedSession.getFileChecksum())
                        )
                ));
            }
        });

        layout.add(headerLayout, grid);

        return layout;
    }

    private Component createFastestLapsLayout(Map<Track, List<Lap>> lapsByTrackMap) {
        List<LapByTrack> fastestLapByTrack = lapsByTrackMap.entrySet().stream()
                .map(entry -> entry.getValue().stream()
                        .filter(Lap::isValid)
                        .filter(lap -> lap.getLapTimeMillis() != null)
                        .min(Comparator.comparing(Lap::getLapTimeMillis))
                        .map(fastest -> LapByTrack.of(entry.getKey(), fastest))
                )
                .flatMap(Optional::stream)   // drop tracks with no valid laps
                .sorted(Comparator.comparing(LapByTrack::track))
                .toList();

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        H3 header = new H3("Fastest laps");

        Grid<LapByTrack> grid = new Grid<>(LapByTrack.class, false);
        grid.addColumn(item -> item.track().getName())
                .setHeader("Track")
                .setSortable(true);
        grid.addColumn(lapByTrack -> AccCar.getCarById(lapByTrack.lap().getCarModelId()).getModel())
                .setHeader("Car Model")
                .setSortable(true);
        grid.addColumn(lapByTrack -> FormatUtils.formatLapTime(lapByTrack.lap().getLapTimeMillis()))
                .setHeader("Lap time")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(lapByTrack -> FormatUtils.formatLapTime(lapByTrack.lap().getSector1Millis()))
                .setHeader("Sector 1")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(lapByTrack -> FormatUtils.formatLapTime(lapByTrack.lap().getSector2Millis()))
                .setHeader("Sector 2")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(lapByTrack -> FormatUtils.formatLapTime(lapByTrack.lap().getSector3Millis()))
                .setHeader("Sector 3")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);

        grid.setItems(fastestLapByTrack);
        grid.setAllRowsVisible(true);
        grid.setMultiSort(true, true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        layout.add(header, grid);

        return layout;
    }

    private Component createFavoriteCarsLayout(List<Lap> laps) {
        Map<Integer, List<Lap>> lapsByCarId = laps.stream()
                .filter(lap -> lap.getCarModelId() != null)
                .collect(Collectors.groupingBy(Lap::getCarModelId));

        List<LapsByAccCar> lapsByAccCar = lapsByCarId.entrySet().stream()
                .map(entry -> LapsByAccCar.of(AccCar.getCarById(entry.getKey()), entry.getValue()))
                .sorted(Comparator.comparing(item -> item.laps().size(), Comparator.reverseOrder()))
                .toList();

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        H3 header = new H3("Favorite cars");

        Grid<LapsByAccCar> grid = new Grid<>(LapsByAccCar.class, false);
        grid.addColumn(item -> item.car().getModel())
                .setHeader("Car Name")
                .setSortable(true);
        grid.addColumn(item -> item.car().getGroup())
                .setHeader("Car Group")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(item -> item.laps().stream().filter(Lap::isValid).count())
                .setHeader("Valid Laps")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(item -> item.laps().stream().filter(lap -> !lap.isValid()).count())
                .setHeader("Invalid Laps")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);

        grid.setItems(lapsByAccCar);
        grid.setAllRowsVisible(true);
        grid.setMultiSort(true, true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setPartNameGenerator(new LapsByCarCarGroupPartNameGenerator());

        layout.add(header, grid);

        return layout;
    }

    private Component createFavoriteTrackLayout(Map<Track, List<Lap>> lapsByTrackMap) {
        List<LapsByTrack> lapsByTrack = lapsByTrackMap.entrySet().stream()
                .map(entry -> LapsByTrack.of(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(item -> item.laps().size(), Comparator.reverseOrder()))
                .toList();

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        H3 header = new H3("Favorite tracks");

        Grid<LapsByTrack> grid = new Grid<>(LapsByTrack.class, false);
        grid.addColumn(item -> item.track().getName())
                .setHeader("Track")
                .setSortable(true);
        grid.addColumn(item -> item.laps().stream().filter(Lap::isValid).count())
                .setHeader("Valid Laps")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(item -> item.laps().stream().filter(lap -> !lap.isValid()).count())
                .setHeader("Invalid Laps")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);

        grid.setItems(lapsByTrack);
        grid.setAllRowsVisible(true);
        grid.setMultiSort(true, true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        layout.add(header, grid);

        return layout;
    }
}
