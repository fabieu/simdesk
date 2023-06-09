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
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.Ranking;
import de.sustineo.acc.leaderboard.filter.RankingFilter;
import de.sustineo.acc.leaderboard.services.DriverService;
import de.sustineo.acc.leaderboard.services.RankingService;
import de.sustineo.acc.leaderboard.views.generators.CarGroupPartNameGenerator;

import java.util.List;
import java.util.function.Consumer;

@Route(value = "ranking/all-time", layout = MainView.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Global Ranking")
@AnonymousAllowed
public class AlltimeRankingView extends VerticalLayout {
    public AlltimeRankingView(RankingService rankingService, DriverService driverService) {
        addClassName("alltime-ranking-view");
        setSizeFull();

        Grid<Ranking> grid = new Grid<>(Ranking.class, false);
        Grid.Column<Ranking> carGroupColumn = grid.addColumn(Ranking::getCarGroup)
                .setResizable(true)
                .setAutoWidth(true)
                .setFlexGrow(0);
        Grid.Column<Ranking> trackNameColumn = grid.addColumn(Ranking::getTrackName)
                .setResizable(true)
                .setAutoWidth(true)
                .setFlexGrow(0);
        Grid.Column<Ranking> lapTimeColumn = grid.addColumn(Ranking::getLapTime)
                .setResizable(true)
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setPartNameGenerator(ranking -> "font-weight-bold");
        Grid.Column<Ranking> driverNameColumn = grid.addColumn((ValueProvider<Ranking, String>) ranking -> driverService.getDriverNameByPlayerId(ranking.getDriverId()))
                .setResizable(true);
        Grid.Column<Ranking> carModelNameColumn = grid.addColumn(Ranking::getCarModelName)
                .setResizable(true);

        List<Ranking> rankings = rankingService.getGlobalRanking();
        GridListDataView<Ranking> dataView = grid.setItems(rankings);
        RankingFilter rankingFilter = new RankingFilter(driverService, dataView);
        grid.setHeightFull();
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setPartNameGenerator(new CarGroupPartNameGenerator());
        grid.getHeaderRows().clear();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(carGroupColumn).setComponent(createFilterHeader("Car Group", rankingFilter::setCarGroup));
        headerRow.getCell(trackNameColumn).setComponent(createFilterHeader("Track name", rankingFilter::setTrackName));
        headerRow.getCell(lapTimeColumn).setComponent(createFilterHeader("Lap Time", null));
        headerRow.getCell(driverNameColumn).setComponent(createFilterHeader("Driver Name", rankingFilter::setDriverName));
        headerRow.getCell(carModelNameColumn).setComponent(createFilterHeader("Car Model", rankingFilter::setCarModelName));

        SingleSelect<Grid<Ranking>, Ranking> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
           Ranking selectedRanking = e.getValue();

           if (selectedRanking != null) {
               getUI().ifPresent(ui -> ui.navigate(MainView.class));
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
