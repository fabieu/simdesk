package de.sustineo.acc.servertools.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.configuration.VaadinConfiguration;
import de.sustineo.acc.servertools.entities.ranking.GroupRanking;
import de.sustineo.acc.servertools.layouts.MainLayout;
import de.sustineo.acc.servertools.services.leaderboard.RankingService;
import de.sustineo.acc.servertools.views.filter.FilterUtils;
import de.sustineo.acc.servertools.views.filter.OverallLapTimesFilter;
import de.sustineo.acc.servertools.views.generators.CarGroupPartNameGenerator;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/lap-times/overall", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_SHORT_PREFIX + "Leaderboard - Lap times")
@AnonymousAllowed
public class OverallLapTimesView extends VerticalLayout {
    private final RankingService rankingService;

    public OverallLapTimesView(RankingService rankingService) {
        this.rankingService = rankingService;

        setSizeFull();
        setPadding(false);

        addAndExpand(createRankingGrid());
    }

    private Component createRankingGrid() {
        List<GroupRanking> groupRankings = rankingService.getAllTimeGroupRanking();

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
        grid.setPartNameGenerator(new CarGroupPartNameGenerator());

        OverallLapTimesFilter overallLapTimesFilter = new OverallLapTimesFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(carGroupColumn).setComponent(FilterUtils.createFilterHeader(overallLapTimesFilter::setCarGroup));
        headerRow.getCell(trackNameColumn).setComponent(FilterUtils.createFilterHeader(overallLapTimesFilter::setTrackName));
        headerRow.getCell(driverNameColumn).setComponent(FilterUtils.createFilterHeader(overallLapTimesFilter::setDriverName));
        headerRow.getCell(carModelNameColumn).setComponent(FilterUtils.createFilterHeader(overallLapTimesFilter::setCarModelName));

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        SingleSelect<Grid<GroupRanking>, GroupRanking> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
            GroupRanking selectedGroupRanking = e.getValue();

            if (selectedGroupRanking != null) {
                getUI().ifPresent(ui -> ui.navigate(OverallLapTimesDifferentiatedView.class,
                        new RouteParameters(
                                new RouteParam(OverallLapTimesDifferentiatedView.ROUTE_PARAMETER_CAR_GROUP, selectedGroupRanking.getCarGroup().name().toLowerCase()),
                                new RouteParam(OverallLapTimesDifferentiatedView.ROUTE_PARAMETER_TRACK_ID, selectedGroupRanking.getTrackId())
                        )
                ));
            }
        });

        return grid;
    }
}
