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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.record.LapsByAccCar;
import de.sustineo.simdesk.entities.record.LapsByTrack;
import de.sustineo.simdesk.services.leaderboard.DriverService;
import de.sustineo.simdesk.services.leaderboard.LapService;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.components.SessionComponentFactory;
import de.sustineo.simdesk.views.filter.GridFilter;
import de.sustineo.simdesk.views.filter.SessionFilter;
import de.sustineo.simdesk.views.generators.LapsByCarCarGroupPartNameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log
@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/drivers/:driverId")
@AnonymousAllowed
@RequiredArgsConstructor
public class LeaderboardDriverView extends BaseView {
    private final DriverService driverService;
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
        List<Lap> lapsByDriver = lapService.getByDriverId(driver.getId());
        List<Session> sessionsByDriver = sessionService.getAllByDriverId(driver.getId());

        Div layout = new Div();
        layout.addClassNames("container", "bg-light");

        layout.add(createBadgeLayout(driver, lapsByDriver));

        layout.add(createFavoriteCarLayout(lapsByDriver));
        layout.add(createFavoriteTrackLayout(lapsByDriver, sessionsByDriver));
        layout.add(createLatestSessionsLayout(sessionsByDriver));

        return layout;
    }

    private Component createBadgeLayout(Driver driver, List<Lap> laps) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setWidthFull();

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

    private Component createLatestSessionsLayout(List<Session> sessions) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        H3 header = new H3("Last sessions");

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
        Grid.Column<Session> sessionTypeColumn = grid.addColumn(session -> session.getSessionType().getDescription())
                .setHeader("Session")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Session> serverNameColumn = grid.addColumn(Session::getServerName)
                .setHeader("Server Name")
                .setSortable(true)
                .setTooltipGenerator(Session::getServerName);
        Grid.Column<Session> trackNameColumn = grid.addColumn(Session::getTrackName)
                .setHeader("Track Name")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);

        GridListDataView<Session> dataView = grid.setItems(sessions);
        grid.setMultiSort(true, true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        SessionFilter sessionFilter = new SessionFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(serverNameColumn).setComponent(GridFilter.createHeader(sessionFilter::setServerName));
        headerRow.getCell(trackNameColumn).setComponent(GridFilter.createHeader(sessionFilter::setTrackName));
        headerRow.getCell(sessionTypeColumn).setComponent(GridFilter.createHeader(sessionFilter::setSessionDescription));

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        SingleSelect<Grid<Session>, Session> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
            Session selectedSession = e.getValue();

            if (selectedSession != null) {
                getUI().ifPresent(ui -> ui.navigate(LeaderboardSessionDetailsView.class,
                        new RouteParameters(
                                new RouteParam(ROUTE_PARAMETER_FILE_CHECKSUM, selectedSession.getFileChecksum())
                        )
                ));
            }
        });

        layout.add(header, grid);

        return layout;
    }

    private Component createFavoriteCarLayout(List<Lap> laps) {
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

    private Component createFavoriteTrackLayout(List<Lap> laps, List<Session> sessions) {
        Map<Integer, Session> sessionByIdMap = sessions.stream()
                .collect(Collectors.toMap(Session::getId, session -> session));

        Map<String, List<Lap>> lapsByTrackId = laps.stream()
                .filter(lap -> lap.getSessionId() != null)
                .collect(Collectors.groupingBy(lap -> sessionByIdMap.get(lap.getSessionId()).getTrackId()));

        List<LapsByTrack> lapsByTrack = lapsByTrackId.entrySet().stream()
                .map(entry -> LapsByTrack.of(Track.getByAccId(entry.getKey()), entry.getValue()))
                .sorted(Comparator.comparing(item -> item.laps().size(), Comparator.reverseOrder()))
                .toList();

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        H3 header = new H3("Favorite tracks");

        Grid<LapsByTrack> grid = new Grid<>(LapsByTrack.class, false);
        grid.addColumn(item -> item.track().getName())
                .setHeader("Track Name")
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
