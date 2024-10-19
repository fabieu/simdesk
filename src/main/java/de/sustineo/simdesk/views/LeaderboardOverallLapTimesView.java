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
import de.sustineo.simdesk.entities.ranking.GroupRanking;
import de.sustineo.simdesk.services.leaderboard.RankingService;
import de.sustineo.simdesk.views.enums.TimeRange;
import de.sustineo.simdesk.views.filter.GridFilter;
import de.sustineo.simdesk.views.filter.OverallLapTimesFilter;
import de.sustineo.simdesk.views.generators.GroupRankingCarGroupPartNameGenerator;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/lap-records")
@PageTitle("Leaderboard - Lap records")
@AnonymousAllowed
public class LeaderboardOverallLapTimesView extends BaseView implements BeforeEnterObserver, AfterNavigationObserver {
    private final RankingService rankingService;

    private Grid<GroupRanking> rankingGrid;
    private TimeRange timeRange = TimeRange.ALL_TIME;
    private RouteParameters routeParameters;
    private QueryParameters queryParameters;

    public LeaderboardOverallLapTimesView(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        routeParameters = beforeEnterEvent.getRouteParameters();
        queryParameters = beforeEnterEvent.getLocation().getQueryParameters();

        Optional<String> timeRange = queryParameters.getSingleParameter(QUERY_PARAMETER_TIME_RANGE);
        if (timeRange.isPresent() && EnumUtils.isValidEnumIgnoreCase(TimeRange.class, timeRange.get())) {
            this.timeRange = EnumUtils.getEnumIgnoreCase(TimeRange.class, timeRange.get());
        }

        this.rankingGrid = createRankingGrid(this.timeRange);

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createViewHeader());
        add(createSelectHeader(this.timeRange));
        addAndExpand(rankingGrid);
        add(createFooter());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateQueryParameters(routeParameters, QueryParameters.of(QUERY_PARAMETER_TIME_RANGE, this.timeRange.name().toLowerCase()));
    }

    private Component createSelectHeader(TimeRange timeRange) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassNames("header", "selection");

        // Time range selection
        Select<TimeRange> timeRangeSelect = new Select<>();
        timeRangeSelect.setItems(TimeRange.values());
        timeRangeSelect.setValue(timeRange);
        timeRangeSelect.addComponents(TimeRange.LAST_WEEK, ComponentUtils.createSpacer());
        timeRangeSelect.setItemLabelGenerator(TimeRange::getDescription);
        timeRangeSelect.addValueChangeListener(event -> {
            replaceRankingGrid(event.getValue());
            updateQueryParameters(routeParameters, QueryParameters.of(QUERY_PARAMETER_TIME_RANGE, event.getValue().name().toLowerCase()));
        });
        layout.add(timeRangeSelect);

        return layout;
    }

    private Grid<GroupRanking> createRankingGrid(TimeRange timeRange) {
        List<GroupRanking> groupRankings = rankingService.getAllTimeGroupRanking(timeRange);

        Grid<GroupRanking> grid = new Grid<>(GroupRanking.class, false);
        Grid.Column<GroupRanking> carGroupColumn = grid.addColumn(GroupRanking::getCarGroup)
                .setHeader("Car Group")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<GroupRanking> trackNameColumn = grid.addColumn(GroupRanking::getTrackName)
                .setHeader("Track")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<GroupRanking> lapTimeColumn = grid.addColumn(GroupRanking::getLapTime)
                .setHeader("Lap Time")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setPartNameGenerator(ranking -> "font-weight-bold");
        Grid.Column<GroupRanking> driverNameColumn = grid.addColumn(groupRanking -> groupRanking.getDriver().getFullName())
                .setHeader("Driver")
                .setSortable(true);
        Grid.Column<GroupRanking> carModelNameColumn = grid.addColumn(GroupRanking::getCarModelName)
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
        headerRow.getCell(carGroupColumn).setComponent(GridFilter.createHeader(overallLapTimesFilter::setCarGroup));
        headerRow.getCell(trackNameColumn).setComponent(GridFilter.createHeader(overallLapTimesFilter::setTrackName));
        headerRow.getCell(driverNameColumn).setComponent(GridFilter.createHeader(overallLapTimesFilter::setDriverName));
        headerRow.getCell(carModelNameColumn).setComponent(GridFilter.createHeader(overallLapTimesFilter::setCarModelName));

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

    private void replaceRankingGrid(TimeRange timeRange) {
        Grid<GroupRanking> grid = createRankingGrid(timeRange);
        replace(this.rankingGrid, grid);
        this.rankingGrid = grid;
    }
}
