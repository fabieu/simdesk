package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.GroupRanking;
import de.sustineo.acc.leaderboard.filter.GroupRankingFilter;
import de.sustineo.acc.leaderboard.services.DriverService;
import de.sustineo.acc.leaderboard.services.RankingService;
import de.sustineo.acc.leaderboard.views.generators.CarGroupPartNameGenerator;

import java.util.List;
import java.util.function.Consumer;

@Route(value = "ranking/all-time", layout = MainView.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "All Time Ranking")
@AnonymousAllowed
public class AllTimeRankingView extends VerticalLayout {
    public AllTimeRankingView(RankingService rankingService, DriverService driverService) {
        addClassName("alltime-ranking-view");
        setSizeFull();

        Grid<GroupRanking> grid = new Grid<>(GroupRanking.class, false);
        Grid.Column<GroupRanking> carGroupColumn = grid.addColumn(GroupRanking::getCarGroup)
                .setResizable(true)
                .setAutoWidth(true)
                .setFlexGrow(0);
        Grid.Column<GroupRanking> trackNameColumn = grid.addColumn(GroupRanking::getTrackName)
                .setResizable(true)
                .setAutoWidth(true)
                .setFlexGrow(0);
        Grid.Column<GroupRanking> lapTimeColumn = grid.addColumn(GroupRanking::getLapTime)
                .setResizable(true)
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setPartNameGenerator(ranking -> "font-weight-bold");
        Grid.Column<GroupRanking> driverNameColumn = grid.addColumn(groupRanking -> groupRanking.getDriver().getFullName())
                .setResizable(true);
        Grid.Column<GroupRanking> carModelNameColumn = grid.addColumn(GroupRanking::getCarModelName)
                .setResizable(true);

        List<GroupRanking> groupRankings = rankingService.getAllTimeGroupRanking();
        GridListDataView<GroupRanking> dataView = grid.setItems(groupRankings);
        GroupRankingFilter groupRankingFilter = new GroupRankingFilter(driverService, dataView);
        grid.setHeightFull();
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setPartNameGenerator(new CarGroupPartNameGenerator());
        grid.getHeaderRows().clear();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(carGroupColumn).setComponent(createFilterHeader("Car Group", groupRankingFilter::setCarGroup));
        headerRow.getCell(trackNameColumn).setComponent(createFilterHeader("Track name", groupRankingFilter::setTrackName));
        headerRow.getCell(lapTimeColumn).setComponent(createFilterHeader("Lap Time", null));
        headerRow.getCell(driverNameColumn).setComponent(createFilterHeader("Driver Name", groupRankingFilter::setDriverName));
        headerRow.getCell(carModelNameColumn).setComponent(createFilterHeader("Car Model", groupRankingFilter::setCarModelName));

        SingleSelect<Grid<GroupRanking>, GroupRanking> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
            GroupRanking selectedGroupRanking = e.getValue();

            if (selectedGroupRanking != null) {
                getUI().ifPresent(ui -> ui.navigate(AllTimeRankingDetailedView.class,
                        new RouteParameters(
                                new RouteParam(AllTimeRankingDetailedView.ROUTE_PARAMETER_CAR_GROUP, selectedGroupRanking.getCarGroup().name().toLowerCase()),
                                new RouteParam(AllTimeRankingDetailedView.ROUTE_PARAMETER_TRACK_ID, selectedGroupRanking.getTrackId())
                        )
                ));
            }
        });

        add(grid);
    }

    private static Component createFilterHeader(String labelText, Consumer<String> filterChangeConsumer) {
        Label label = new Label(labelText);
        label.getStyle()
                .set("font-size", "var(--lumo-font-size-m)");
        VerticalLayout layout = new VerticalLayout(label);

        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        if (filterChangeConsumer != null) {
            textField.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        } else {
            textField.setEnabled(false);
        }
        layout.add(textField);

        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }
}
