package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.ranking.DriverRanking;
import de.sustineo.simdesk.services.leaderboard.RankingService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.enums.TimeRange;
import de.sustineo.simdesk.views.filter.GridFilter;
import de.sustineo.simdesk.views.filter.OverallLapTimesDifferentiatedFilter;
import de.sustineo.simdesk.views.generators.DriverRankingPodiumPartNameGenerator;
import de.sustineo.simdesk.views.renderers.DriverRankingRenderer;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/lap-records/:carGroup/:trackId")
@PageTitle("Leaderboard - All lap records")
@AnonymousAllowed
public class LeaderboardOverallLapTimesDifferentiatedView extends BaseView implements BeforeEnterObserver, AfterNavigationObserver {
    private final RankingService rankingService;

    private Grid<DriverRanking> rankingGrid;
    private TimeRange timeRange = TimeRange.ALL_TIME;
    private RouteParameters routeParameters;
    private QueryParameters queryParameters;

    public LeaderboardOverallLapTimesDifferentiatedView(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        routeParameters = beforeEnterEvent.getRouteParameters();
        queryParameters = beforeEnterEvent.getLocation().getQueryParameters();

        String carGroup = routeParameters.get(ROUTE_PARAMETER_CAR_GROUP).orElseThrow();
        String trackId = routeParameters.get(ROUTE_PARAMETER_TRACK_ID).orElseThrow();
        Optional<String> timeRange = queryParameters.getSingleParameter(QUERY_PARAMETER_TIME_RANGE);

        if (Track.existsInAcc(trackId) && CarGroup.exists(carGroup)) {
            if (timeRange.isPresent() && EnumUtils.isValidEnumIgnoreCase(TimeRange.class, timeRange.get())) {
                this.timeRange = EnumUtils.getEnumIgnoreCase(TimeRange.class, timeRange.get());
            }

            this.rankingGrid = createRankingGrid(EnumUtils.getEnumIgnoreCase(CarGroup.class, carGroup), trackId, this.timeRange);

            setSizeFull();
            setSpacing(false);
            setPadding(false);

            removeAll();

            add(createViewHeader(String.format("%s on %s (%s)", getAnnotatedPageTitle(), Track.getTrackNameByAccId(trackId), carGroup.toUpperCase())));
            add(createSelectHeader(carGroup, trackId, this.timeRange));
            addAndExpand(this.rankingGrid);
        } else {
            beforeEnterEvent.rerouteToError(NotFoundException.class);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateQueryParameters(routeParameters, QueryParameters.of(QUERY_PARAMETER_TIME_RANGE, this.timeRange.name().toLowerCase()));
    }

    private Component createSelectHeader(String carGroup, String trackId, TimeRange timeRange) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassNames("header");
        layout.setJustifyContentMode(JustifyContentMode.END);
        layout.getStyle()
                .setPaddingTop("0")
                .setMarginBottom("0");

        Select<TimeRange> timeRangeSelect = new Select<>();
        timeRangeSelect.setItems(TimeRange.values());
        timeRangeSelect.setValue(timeRange);
        timeRangeSelect.addComponents(TimeRange.LAST_WEEK, ComponentUtils.createSpacer());
        timeRangeSelect.setItemLabelGenerator(TimeRange::getDescription);
        timeRangeSelect.addValueChangeListener(event -> {
            replaceRankingGrid(EnumUtils.getEnumIgnoreCase(CarGroup.class, carGroup), trackId, event.getValue());
            updateQueryParameters(routeParameters, QueryParameters.of(QUERY_PARAMETER_TIME_RANGE, this.timeRange.name().toLowerCase()));
        });

        layout.add(timeRangeSelect);
        return layout;
    }

    private Grid<DriverRanking> createRankingGrid(CarGroup carGroup, String trackId, TimeRange timeRange) {
        List<DriverRanking> driverRankings = rankingService.getAllTimeDriverRanking(carGroup, trackId, timeRange);
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
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> sessionInformationColumn = grid.addColumn(DriverRankingRenderer.createSessionRenderer())
                .setHeader("Session")
                .setSortable(true);
        Grid.Column<DriverRanking> sessionDatetimeColumn = grid.addColumn(driverRanking -> FormatUtils.formatDatetime(driverRanking.getSession().getSessionDatetime()))
                .setHeader("Session Time")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(driverRanking -> driverRanking.getSession().getSessionDatetime());

        GridListDataView<DriverRanking> dataView = grid.setItems(driverRankings);
        grid.setSizeFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        OverallLapTimesDifferentiatedFilter overallLapTimesDifferentiatedFilter = new OverallLapTimesDifferentiatedFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(driverNameColumn).setComponent(GridFilter.createHeader(overallLapTimesDifferentiatedFilter::setDriverName));
        headerRow.getCell(carModelColumn).setComponent(GridFilter.createHeader(overallLapTimesDifferentiatedFilter::setCarModelName));
        headerRow.getCell(sessionInformationColumn).setComponent(GridFilter.createHeader(overallLapTimesDifferentiatedFilter::setSessionDescription));

        return grid;
    }

    private void replaceRankingGrid(CarGroup carGroup, String trackId, TimeRange timeRange) {
        Grid<DriverRanking> grid = createRankingGrid(carGroup, trackId, timeRange);
        replace(this.rankingGrid, grid);
        this.rankingGrid = grid;
    }
}
