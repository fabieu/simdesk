package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.DriverRanking;
import de.sustineo.acc.leaderboard.entities.Track;
import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import de.sustineo.acc.leaderboard.filter.DriverRankingFilter;
import de.sustineo.acc.leaderboard.services.DriverService;
import de.sustineo.acc.leaderboard.services.RankingService;
import de.sustineo.acc.leaderboard.views.generators.PodiumPartNameGenerator;

import java.util.List;
import java.util.function.Consumer;

@Route(value = "ranking/all-time/:carGroup/:trackId", layout = MainView.class)
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
            add(createRankingGrid(carGroup, trackId));
        } else {
            event.rerouteToError(NotFoundException.class);
        }
    }

    private Component createRankingGrid(String carGroup, String trackId) {
        Grid<DriverRanking> grid = new Grid<>(DriverRanking.class, false);
        Grid.Column<DriverRanking> rankingColumn = grid.addColumn(DriverRanking::getRanking)
                .setHeader("#")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<DriverRanking> driverNameColumn = grid.addColumn(DriverRanking::getDriverFullName)
                .setHeader("Driver")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> lapTimeColumn = grid.addColumn(DriverRanking::getLapTime)
                .setHeader("Lap Time")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setPartNameGenerator(ranking -> "font-weight-bold")
                .setSortable(true);
        Grid.Column<DriverRanking> split1Column = grid.addColumn(DriverRanking::getSplit1)
                .setHeader("Split 1")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> split2Column = grid.addColumn(DriverRanking::getSplit2)
                .setHeader("Split 2")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> split3Column = grid.addColumn(DriverRanking::getSplit3)
                .setHeader("Split 3")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> carModelColumn = grid.addColumn(DriverRanking::getCarModelName)
                .setHeader("Car Model")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<DriverRanking> sessionColumn = grid.addColumn(DriverRanking::getSessionDescription)
                .setHeader("Session")
                .setResizable(true);
        Grid.Column<DriverRanking> lapCountColumn = grid.addColumn(DriverRanking::getLapCount)
                .setHeader("Laps")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END);

        List<DriverRanking> driverRankings = rankingService.getAllTimeDriverRanking(carGroup, trackId);
        GridListDataView<DriverRanking> dataView = grid.setItems(driverRankings);
        DriverRankingFilter driverRankingFilter = new DriverRankingFilter(driverService, dataView);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setPartNameGenerator(new PodiumPartNameGenerator());

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(driverNameColumn).setComponent(createFilterHeader(driverRankingFilter::setDriverName));
        headerRow.getCell(carModelColumn).setComponent(createFilterHeader(driverRankingFilter::setCarModelName));
        headerRow.getCell(sessionColumn).setComponent(createFilterHeader(driverRankingFilter::setSessionDescription));

        return grid;
    }

    private static Component createFilterHeader(Consumer<String> filterChangeConsumer) {
        VerticalLayout layout = new VerticalLayout();

        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        textField.setWidthFull();
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setClearButtonVisible(true);
        textField.setPlaceholder("Search");
        textField.setPrefixComponent(VaadinIcon.SEARCH.create());
        layout.add(textField);

        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }
}
