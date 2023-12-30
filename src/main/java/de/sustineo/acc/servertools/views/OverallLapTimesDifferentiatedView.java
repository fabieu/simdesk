package de.sustineo.acc.servertools.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.configuration.VaadinConfiguration;
import de.sustineo.acc.servertools.entities.Track;
import de.sustineo.acc.servertools.entities.enums.CarGroup;
import de.sustineo.acc.servertools.entities.ranking.DriverRanking;
import de.sustineo.acc.servertools.layouts.MainLayout;
import de.sustineo.acc.servertools.services.leaderboard.RankingService;
import de.sustineo.acc.servertools.views.filter.FilterUtils;
import de.sustineo.acc.servertools.views.filter.OverallLapTimesDifferentiatedFilter;
import de.sustineo.acc.servertools.views.generators.DriverRankingPodiumPartNameGenerator;
import de.sustineo.acc.servertools.views.renderers.DriverRankingRenderer;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/lap-times/overall/:carGroup/:trackId", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_SHORT_PREFIX + "Leaderboard - Lap times by track")
@AnonymousAllowed
public class OverallLapTimesDifferentiatedView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE_PARAMETER_CAR_GROUP = "carGroup";
    public static final String ROUTE_PARAMETER_TRACK_ID = "trackId";
    private final RankingService rankingService;

    public OverallLapTimesDifferentiatedView(RankingService rankingService) {
        this.rankingService = rankingService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final RouteParameters routeParameters = event.getRouteParameters();

        String carGroup = routeParameters.get(ROUTE_PARAMETER_CAR_GROUP).orElseThrow();
        String trackId = routeParameters.get(ROUTE_PARAMETER_TRACK_ID).orElseThrow();

        if (Track.isValid(trackId) && CarGroup.isValid(carGroup)) {
            add(createRankingHeader(carGroup, trackId));
            addAndExpand(createRankingGrid(carGroup, trackId));
        } else {
            event.rerouteToError(NotFoundException.class);
        }
    }

    private Component createRankingHeader(String carGroup, String trackId) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setPadding(true);

        // Header displaying the car group and track name
        H3 heading = new H3(carGroup.toUpperCase() + " - " + Track.getTrackNameById(trackId));

        layout.add(heading);
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
                .setTextAlign(ColumnTextAlign.CENTER)
                .setPartNameGenerator(new DriverRankingPodiumPartNameGenerator());
        Grid.Column<DriverRanking> driverNameColumn = grid.addColumn(driverRanking -> driverRanking.getDriver().getFullName())
                .setHeader("Driver")
                .setAutoWidth(true)
                .setFlexGrow(1)
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
                .setHeader("Session")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> serverNameColumn = grid.addColumn(driverRanking -> driverRanking.getSession().getServerName())
                .setHeader("Server Name")
                .setSortable(true)
                .setFlexGrow(2);

        GridListDataView<DriverRanking> dataView = grid.setItems(driverRankings);
        grid.setSizeFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        OverallLapTimesDifferentiatedFilter overallLapTimesDifferentiatedFilter = new OverallLapTimesDifferentiatedFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(driverNameColumn).setComponent(FilterUtils.createFilterHeader(overallLapTimesDifferentiatedFilter::setDriverName));
        headerRow.getCell(carModelColumn).setComponent(FilterUtils.createFilterHeader(overallLapTimesDifferentiatedFilter::setCarModelName));
        headerRow.getCell(serverNameColumn).setComponent(FilterUtils.createFilterHeader(overallLapTimesDifferentiatedFilter::setServerName));
        headerRow.getCell(sessionTypeDescriptionColumn).setComponent(FilterUtils.createFilterHeader(overallLapTimesDifferentiatedFilter::setSessionTypeDescription));

        return grid;
    }
}
