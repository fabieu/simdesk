package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.ranking.GroupRanking;
import de.sustineo.acc.leaderboard.layouts.MainLayout;
import de.sustineo.acc.leaderboard.services.RankingService;
import de.sustineo.acc.leaderboard.views.generators.CarGroupPartNameGenerator;

import java.util.List;

@Route(value = "ranking/all-time", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "All Time Ranking")
@AnonymousAllowed
public class AllTimeGroupRankingView extends VerticalLayout {
    private final RankingService rankingService;

    public AllTimeGroupRankingView(RankingService rankingService, ComponentUtils componentUtils) {
        this.rankingService = rankingService;
        addClassName("alltime-ranking-view");
        setSizeFull();

        addAndExpand(createRankingGrid());
        add(componentUtils.createFooter());
    }

    private Component createRankingGrid() {
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
        Grid.Column<GroupRanking> driverNameColumn = grid.addColumn(groupRanking -> groupRanking.getDriver().getEntireName())
                .setHeader("Driver")
                .setSortable(true);
        Grid.Column<GroupRanking> carModelNameColumn = grid.addColumn(GroupRanking::getCarModelName)
                .setHeader("Car Model")
                .setSortable(true);

        List<GroupRanking> groupRankings = rankingService.getAllTimeGroupRanking();
        grid.setItems(groupRankings);
        grid.setHeightFull();
        grid.setColumnReorderingAllowed(true);
        grid.setMultiSort(true, true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setPartNameGenerator(new CarGroupPartNameGenerator());
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        SingleSelect<Grid<GroupRanking>, GroupRanking> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
            GroupRanking selectedGroupRanking = e.getValue();

            if (selectedGroupRanking != null) {
                getUI().ifPresent(ui -> ui.navigate(AllTimeDriverRankingView.class,
                        new RouteParameters(
                                new RouteParam(AllTimeDriverRankingView.ROUTE_PARAMETER_CAR_GROUP, selectedGroupRanking.getCarGroup().name().toLowerCase()),
                                new RouteParam(AllTimeDriverRankingView.ROUTE_PARAMETER_TRACK_ID, selectedGroupRanking.getTrackId())
                        )
                ));
            }
        });

        return grid;
    }
}
