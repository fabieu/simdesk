package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.DriverRanking;
import de.sustineo.acc.leaderboard.entities.Track;
import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import de.sustineo.acc.leaderboard.layouts.MainLayout;
import de.sustineo.acc.leaderboard.services.DriverService;
import de.sustineo.acc.leaderboard.services.RankingService;
import de.sustineo.acc.leaderboard.views.filter.DriverRankingFilter;
import de.sustineo.acc.leaderboard.views.filter.FilterUtils;
import de.sustineo.acc.leaderboard.views.generators.PodiumPartNameGenerator;
import de.sustineo.acc.leaderboard.views.renderers.DriverRankingRenderer;

import java.util.List;

@Route(value = "ranking/all-time/:carGroup/:trackId", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "All Time Ranking")
@AnonymousAllowed
public class AllTimeDriverRankingView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE_PARAMETER_CAR_GROUP = "carGroup";
    public static final String ROUTE_PARAMETER_TRACK_ID = "trackId";
    private final RankingService rankingService;
    private final DriverService driverService;

    public AllTimeDriverRankingView(RankingService rankingService, DriverService driverService) {
        this.rankingService = rankingService;
        this.driverService = driverService;
        addClassName("alltime-ranking-detailed-view");
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final RouteParameters routeParameters = event.getRouteParameters();

        String carGroup = routeParameters.get(ROUTE_PARAMETER_CAR_GROUP).orElseThrow();
        String trackId = routeParameters.get(ROUTE_PARAMETER_TRACK_ID).orElseThrow();

        if (Track.isValid(trackId) && CarGroup.isValid(carGroup)) {
            add(createRankingHeader(carGroup, trackId));
            addAndExpand(createRankingGrid(carGroup, trackId));
            add(ComponentUtils.createFooter());
        } else {
            event.rerouteToError(NotFoundException.class);
        }
    }

    private Component createRankingHeader(String carGroup, String trackId) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        // Header displaying the car group and track name
        H1 header = new H1();
        header.setText(CarGroup.of(carGroup) + " - " + Track.getTrackNameById(trackId));
        header.getStyle().set("font-size", "var(--lumo-font-size-xxl)");

        layout.add(header);

        return layout;
    }

    private Component createRankingGrid(String carGroup, String trackId) {
        List<DriverRanking> driverRankings = rankingService.getAllTimeDriverRanking(carGroup, trackId);
        DriverRanking topDriverRanking = driverRankings.stream().findFirst().orElse(null);

        Grid<DriverRanking> grid = new Grid<>(DriverRanking.class, false);
        Grid.Column<DriverRanking> rankingColumn = grid.addColumn(DriverRanking::getRanking)
                .setHeader("#")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<DriverRanking> weatherColumn = grid.addComponentColumn(driverRanking -> ComponentUtils.getWeatherIcon(driverRanking.getSession()))
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<DriverRanking> driverNameColumn = grid.addColumn(DriverRanking::getDriverFullName)
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
        Grid.Column<DriverRanking> split1Column = grid.addColumn(DriverRankingRenderer.createSplit1Renderer(topDriverRanking))
                .setHeader("Split 1")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(DriverRanking::getSplit1Millis);
        Grid.Column<DriverRanking> split2Column = grid.addColumn(DriverRankingRenderer.createSplit2Renderer(topDriverRanking))
                .setHeader("Split 2")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(DriverRanking::getSplit2Millis);
        Grid.Column<DriverRanking> split3Column = grid.addColumn(DriverRankingRenderer.createSplit3Renderer(topDriverRanking))
                .setHeader("Split 3")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(DriverRanking::getSplit3Millis);
        Grid.Column<DriverRanking> carModelColumn = grid.addColumn(DriverRanking::getCarModelName)
                .setHeader("Car Model")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> sessionTypeDescriptionColumn = grid.addColumn(driverRanking -> driverRanking.getSession().getSessionType().getDescription())
                .setHeader("Session Type")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> serverNameColumn = grid.addColumn(driverRanking -> driverRanking.getSession().getServerName())
                .setHeader("Server Name")
                .setSortable(true);
        Grid.Column<DriverRanking> lapCountColumn = grid.addColumn(DriverRanking::getLapCount)
                .setHeader("Laps")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.END);

        GridListDataView<DriverRanking> dataView = grid.setItems(driverRankings);
        DriverRankingFilter driverRankingFilter = new DriverRankingFilter(driverService, dataView);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setPartNameGenerator(new PodiumPartNameGenerator());

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(driverNameColumn).setComponent(FilterUtils.createFilterHeader(driverRankingFilter::setDriverName));
        headerRow.getCell(carModelColumn).setComponent(FilterUtils.createFilterHeader(driverRankingFilter::setCarModelName));
        headerRow.getCell(sessionTypeDescriptionColumn).setComponent(FilterUtils.createFilterHeader(driverRankingFilter::setServerName));
        headerRow.getCell(serverNameColumn).setComponent(FilterUtils.createFilterHeader(driverRankingFilter::setSessionTypeDescription));

        return grid;
    }
}
