package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.ranking.GroupRanking;
import de.sustineo.simdesk.services.leaderboard.RankingService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.components.ComponentFactory;
import de.sustineo.simdesk.views.enums.TimeRange;
import de.sustineo.simdesk.views.filter.grid.GridFilter;
import de.sustineo.simdesk.views.filter.grid.OverallLapTimesFilter;
import de.sustineo.simdesk.views.generators.GroupRankingCarGroupPartNameGenerator;
import de.sustineo.simdesk.views.renderers.GroupRankingRenderer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/lap-records")
@AnonymousAllowed
@RequiredArgsConstructor
public class LeaderboardOverallLapTimesView extends BaseView {
    private final RankingService rankingService;

    private final ComponentFactory componentFactory;

    private Grid<GroupRanking> rankingGrid;
    private TimeRange timeRange = TimeRange.ALL_TIME;

    @Override
    public String getPageTitle() {
        return "Leaderboard - Lap records";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        routeParameters = beforeEnterEvent.getRouteParameters();
        queryParameters = beforeEnterEvent.getLocation().getQueryParameters();

        Optional<String> timeRange = queryParameters.getSingleParameter(QUERY_PARAMETER_TIME_RANGE);
        if (timeRange.isPresent() && EnumUtils.isValidEnumIgnoreCase(TimeRange.class, timeRange.get())) {
            this.timeRange = EnumUtils.getEnumIgnoreCase(TimeRange.class, timeRange.get());
        }

        this.rankingGrid = createRankingGrid();

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        removeAll();

        add(createViewHeader());
        add(createSelectHeader());
        addAndExpand(rankingGrid);
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

        // Time range selection
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

    private Grid<GroupRanking> createRankingGrid() {
        List<GroupRanking> groupRankings = rankingService.getAllTimeGroupRanking(timeRange);

        Grid<GroupRanking> grid = new Grid<>(GroupRanking.class, false);
        Grid.Column<GroupRanking> carGroupColumn = grid.addColumn(GroupRanking::getCarGroup)
                .setHeader("Car Group")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<GroupRanking> trackColumn = grid.addColumn(groupRanking -> Track.getByAccId(groupRanking.getTrackId()))
                .setHeader("Track")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<GroupRanking> lapTimeColumn = grid.addColumn(groupRanking -> FormatUtils.formatLapTime(groupRanking.getLapTimeMillis()))
                .setHeader("Lap Time")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setPartNameGenerator(ranking -> "font-weight-bold");
        Grid.Column<GroupRanking> driverNameColumn = grid.addColumn(GroupRankingRenderer.createDriverRenderer())
                .setHeader("Driver")
                .setSortable(true);
        Grid.Column<GroupRanking> carModelNameColumn = grid.addColumn(groupRanking -> AccCar.getModelById(groupRanking.getCarModelId()))
                .setHeader("Car Model")
                .setSortable(true);


        GridListDataView<GroupRanking> dataView = grid.setItems(groupRankings);
        grid.setSizeFull();
        grid.setColumnReorderingAllowed(true);
        grid.setMultiSort(true, true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setPartNameGenerator(new GroupRankingCarGroupPartNameGenerator());

        OverallLapTimesFilter overallLapTimesFilter = new OverallLapTimesFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(carGroupColumn).setComponent(GridFilter.createComboBoxHeader(overallLapTimesFilter::setCarGroup, CarGroup::getValid));
        headerRow.getCell(trackColumn).setComponent(GridFilter.createComboBoxHeader(overallLapTimesFilter::setTrack, Track::getAllOfAccSortedByName));
        headerRow.getCell(driverNameColumn).setComponent(GridFilter.createTextFieldHeader(overallLapTimesFilter::setDriverName));
        headerRow.getCell(carModelNameColumn).setComponent(GridFilter.createTextFieldHeader(overallLapTimesFilter::setCarModelName));

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        SingleSelect<Grid<GroupRanking>, GroupRanking> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
            GroupRanking selectedGroupRanking = e.getValue();

            if (selectedGroupRanking != null) {
                getUI().ifPresent(ui -> ui.navigate(LeaderboardOverallLapTimesDifferentiatedView.class,
                        new RouteParameters(
                                new RouteParam(ROUTE_PARAMETER_CAR_GROUP, selectedGroupRanking.getCarGroup().name().toLowerCase()),
                                new RouteParam(ROUTE_PARAMETER_TRACK_ID, selectedGroupRanking.getTrackId())
                        ),
                        new QueryParameters(Map.of(QUERY_PARAMETER_TIME_RANGE, List.of(timeRange.name().toLowerCase())))
                ));
            }
        });

        return grid;
    }

    private void reloadRankingGrid() {
        Grid<GroupRanking> grid = createRankingGrid();
        replace(rankingGrid, grid);

        this.rankingGrid = grid;
    }

    private void updateQueryParameters() {
        updateQueryParameters(routeParameters, QueryParameters.of(QUERY_PARAMETER_TIME_RANGE, timeRange.name().toLowerCase()));
    }
}
