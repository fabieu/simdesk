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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.entities.Ranking;
import de.sustineo.acc.leaderboard.filter.RankingFilter;
import de.sustineo.acc.leaderboard.services.DriverService;
import de.sustineo.acc.leaderboard.services.RankingService;
import de.sustineo.acc.leaderboard.views.generators.RankingPartNameGenerator;

import java.util.List;
import java.util.function.Consumer;

@Route(value = "ranking/all-time", layout = MainView.class)
@PageTitle("Global Ranking")
@AnonymousAllowed
public class RankingGlobalView extends VerticalLayout {
    public RankingGlobalView(RankingService rankingService, DriverService driverService) {

        Grid<Ranking> grid = new Grid<>(Ranking.class, false);
        Grid.Column<Ranking> carGroupColumn = grid.addColumn(Ranking::getCarGroup)
                .setAutoWidth(true)
                .setFlexGrow(0);
        Grid.Column<Ranking> trackNameColumn = grid.addColumn(Ranking::getTrackName)
                .setAutoWidth(true)
                .setFlexGrow(0);
        Grid.Column<Ranking> lapTimeColumn = grid.addColumn(Ranking::getLapTime)
                .setAutoWidth(true)
                .setFlexGrow(0);
        Grid.Column<Ranking> driverNameColumn = grid.addColumn((ValueProvider<Ranking, String>) ranking -> driverService.getDriverNameByPlayerId(ranking.getDriverId()));
        Grid.Column<Ranking> carModelNameColumn = grid.addColumn(Ranking::getCarModelName);

        List<Ranking> rankings = rankingService.getGlobalRanking();
        GridListDataView<Ranking> dataView = grid.setItems(rankings);
        RankingFilter rankingFilter = new RankingFilter(driverService, dataView);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setPartNameGenerator(new RankingPartNameGenerator());
        grid.getHeaderRows().clear();


        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(carGroupColumn).setComponent(createFilterHeader("Car Group", rankingFilter::setCarGroup));
        headerRow.getCell(trackNameColumn).setComponent(createFilterHeader("Track name", rankingFilter::setTrackName));
        headerRow.getCell(lapTimeColumn).setComponent(createFilterHeader("Lap Time", null));
        headerRow.getCell(driverNameColumn).setComponent(createFilterHeader("Driver Name", rankingFilter::setDriverName));
        headerRow.getCell(carModelNameColumn).setComponent(createFilterHeader("Car Model", rankingFilter::setCarModelName));

        add(grid);
    }

    private static Component createFilterHeader(String labelText, Consumer<String> filterChangeConsumer) {
        Label label = new Label(labelText);
        label.getStyle()
                .set("font-size", "var(--lumo-font-size-m)");
        VerticalLayout layout = new VerticalLayout(label);

        boolean enabled = filterChangeConsumer != null;

        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        textField.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        textField.setEnabled(enabled);
        layout.add(textField);

        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }
}
