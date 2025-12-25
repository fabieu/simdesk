package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.RaceTrack;
import de.sustineo.simdesk.entities.RaceTracks;
import de.sustineo.simdesk.entities.Simulation;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.ranking.DriverRanking;
import de.sustineo.simdesk.services.leaderboard.RankingService;
import de.sustineo.simdesk.views.components.ComponentFactory;
import de.sustineo.simdesk.views.enums.TimeRange;
import de.sustineo.simdesk.views.filter.grid.GridFilter;
import de.sustineo.simdesk.views.filter.grid.OverallLapTimesDifferentiatedFilter;
import de.sustineo.simdesk.views.generators.DriverRankingPodiumPartNameGenerator;
import de.sustineo.simdesk.views.renderers.DriverRankingRenderer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.context.annotation.Profile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Profile(SpringProfile.LEADERBOARD)
@Route(value = "/leaderboard/lap-records/:carGroup/:trackId")
@AnonymousAllowed
@RequiredArgsConstructor
public class LeaderboardOverallLapTimesDifferentiatedView extends BaseView {
    private final RankingService rankingService;

    private final ComponentFactory componentFactory;

    private Grid<DriverRanking> rankingGrid;

    private RaceTrack raceTrack;
    private CarGroup carGroup;

    private TimeRange timeRange = TimeRange.ALL_TIME;
    private AccCar car;

    @Override
    public String getPageTitle() {
        return String.format("Leaderboard - %s (%s)", this.raceTrack.getDisplayName(), this.carGroup.name());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        routeParameters = beforeEnterEvent.getRouteParameters();
        queryParameters = beforeEnterEvent.getLocation().getQueryParameters();

        String carGroup = routeParameters.get(ROUTE_PARAMETER_CAR_GROUP).orElseThrow();
        String trackId = routeParameters.get(ROUTE_PARAMETER_TRACK_ID).orElseThrow();

        if (!RaceTracks.exists(Simulation.ACC, trackId) || !CarGroup.exists(carGroup)) {
            beforeEnterEvent.rerouteToError(NotFoundException.class);
            return;
        }

        this.raceTrack = RaceTracks.getById(Simulation.ACC, trackId);
        this.carGroup = EnumUtils.getEnumIgnoreCase(CarGroup.class, carGroup);

        Optional<String> timeRange = queryParameters.getSingleParameter(QUERY_PARAMETER_TIME_RANGE);
        if (timeRange.isPresent() && EnumUtils.isValidEnumIgnoreCase(TimeRange.class, timeRange.get())) {
            this.timeRange = EnumUtils.getEnumIgnoreCase(TimeRange.class, timeRange.get());
        }

        Optional<String> car = queryParameters.getSingleParameter(QUERY_PARAMETER_CAR_ID);
        try {
            if (car.isPresent() && !car.get().isEmpty()) {
                this.car = AccCar.getCarById(Integer.parseInt(car.get()));
            }
        } catch (NumberFormatException e) {
            this.car = null; // If parsing fails, set car to null
        }


        this.rankingGrid = createRankingGrid();

        setSizeFull();
        setSpacing(false);
        setPadding(false);

        removeAll();

        add(createViewHeader());
        add(createSelectHeader());
        addAndExpand(this.rankingGrid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateQueryParameters();
    }

    private Component createSelectHeader() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassNames("header");
        layout.setJustifyContentMode(JustifyContentMode.END);
        layout.getStyle()
                .setPaddingTop("0")
                .setMarginBottom("0");

        // Car selection
        ComboBox<AccCar> carComboBox = new ComboBox<>();
        carComboBox.setItems(AccCar.getAllByGroup(carGroup));
        carComboBox.setValue(car);
        carComboBox.setPlaceholder("All cars");
        carComboBox.setItemLabelGenerator(AccCar::getModel);
        carComboBox.setClassNameGenerator(item -> item.getGroup().name());
        carComboBox.setClearButtonVisible(true);
        carComboBox.setMinWidth("300px");
        carComboBox.addValueChangeListener(event -> {
            this.car = event.getValue();
            reloadRankingGrid();
            updateQueryParameters();
        });
        layout.add(carComboBox);

        Select<TimeRange> timeRangeSelect = new Select<>();
        timeRangeSelect.setItems(TimeRange.values());
        timeRangeSelect.setValue(timeRange);
        timeRangeSelect.addComponents(TimeRange.LAST_WEEK, componentFactory.createSpacer());
        timeRangeSelect.setItemLabelGenerator(TimeRange::getDescription);
        timeRangeSelect.addValueChangeListener(event -> {
            this.timeRange = event.getValue();
            reloadRankingGrid();
            updateQueryParameters();
        });

        layout.add(timeRangeSelect);
        return layout;
    }

    private Grid<DriverRanking> createRankingGrid() {
        List<DriverRanking> driverRankings = rankingService.getAllTimeDriverRanking(carGroup, raceTrack.getId(Simulation.ACC), timeRange, car);
        DriverRanking topDriverRanking = driverRankings.stream().findFirst().orElse(null);

        Grid<DriverRanking> grid = new Grid<>(DriverRanking.class, false);
        Grid.Column<DriverRanking> rankingColumn = grid.addColumn(DriverRanking::getRanking)
                .setHeader("#")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setPartNameGenerator(new DriverRankingPodiumPartNameGenerator());
        Grid.Column<DriverRanking> driverNameColumn = grid.addColumn(DriverRankingRenderer.createDriverRenderer())
                .setHeader("Driver")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> lapTimeColumn = grid.addColumn(DriverRankingRenderer.createLapTimeRenderer(topDriverRanking))
                .setHeader("Lap Time")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setPartNameGenerator(ranking -> "font-weight-bold")
                .setSortable(true)
                .setComparator(DriverRanking::getLapTimeMillis);
        Grid.Column<DriverRanking> sector1Column = grid.addColumn(DriverRankingRenderer.createSector1Renderer(topDriverRanking))
                .setHeader("Sector 1")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(DriverRanking::getSector1Millis);
        Grid.Column<DriverRanking> sector2Column = grid.addColumn(DriverRankingRenderer.createSector2Renderer(topDriverRanking))
                .setHeader("Sector 2")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(DriverRanking::getSector2Millis);
        Grid.Column<DriverRanking> sector3Column = grid.addColumn(DriverRankingRenderer.createSector3Renderer(topDriverRanking))
                .setHeader("Sector 3")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(DriverRanking::getSector3Millis);
        Grid.Column<DriverRanking> carModelColumn = grid.addColumn(driverRanking -> AccCar.getModelById(driverRanking.getCarModelId()))
                .setHeader("Car Model")
                .setSortable(true);
        Grid.Column<DriverRanking> theoreticalBestLapTimeColumn = grid.addColumn(DriverRankingRenderer.createTheoreticalBestLapTimeRenderer())
                .setHeader("Theoretical Best")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(driverRanking -> driverRanking.getBestSectors().getTheoreticalBestLapMillis());
        Grid.Column<DriverRanking> bestSector1Column = grid.addColumn(DriverRankingRenderer.createBestSector1Renderer())
                .setHeader("Best Sector 1")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(driverRanking -> driverRanking.getBestSectors().getBestSector1Millis());
        Grid.Column<DriverRanking> bestSector2Column = grid.addColumn(DriverRankingRenderer.createBestSector2Renderer())
                .setHeader("Best Sector 2")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(driverRanking -> driverRanking.getBestSectors().getBestSector2Millis());
        Grid.Column<DriverRanking> bestSector3Column = grid.addColumn(DriverRankingRenderer.createBestSector3Renderer())
                .setHeader("Best Sector 3")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(driverRanking -> driverRanking.getBestSectors().getBestSector3Millis());

        GridListDataView<DriverRanking> dataView = grid.setItems(driverRankings);
        grid.setSizeFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        SingleSelect<Grid<DriverRanking>, DriverRanking> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
            DriverRanking selectedDriverRanking = e.getValue();

            if (selectedDriverRanking != null && selectedDriverRanking.getSession() != null) {
                getUI().ifPresent(ui -> ui.navigate(LeaderboardSessionDetailView.class,
                        new RouteParameters(
                                new RouteParam(ROUTE_PARAMETER_FILE_CHECKSUM, selectedDriverRanking.getSession().getFileChecksum())
                        )
                ));
            }
        });

        OverallLapTimesDifferentiatedFilter overallLapTimesDifferentiatedFilter = new OverallLapTimesDifferentiatedFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(driverNameColumn).setComponent(GridFilter.createTextFieldHeader(overallLapTimesDifferentiatedFilter::setDriverName));
        headerRow.getCell(carModelColumn).setComponent(GridFilter.createTextFieldHeader(overallLapTimesDifferentiatedFilter::setCarModelName));

        return grid;
    }

    private void reloadRankingGrid() {
        Grid<DriverRanking> grid = createRankingGrid();
        replace(rankingGrid, grid);

        this.rankingGrid = grid;
    }

    private void updateQueryParameters() {
        Map<String, List<String>> queryParams = new LinkedHashMap<>();
        if (car != null) {
            queryParams.put(QUERY_PARAMETER_CAR_ID, List.of(String.valueOf(car.getId())));
        }

        queryParams.put(QUERY_PARAMETER_TIME_RANGE, List.of(timeRange.name().toLowerCase()));

        updateQueryParameters(routeParameters, new QueryParameters(queryParams));
    }
}
