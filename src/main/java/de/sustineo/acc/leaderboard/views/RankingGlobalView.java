package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.entities.Lap;
import de.sustineo.acc.leaderboard.services.LapService;

import java.util.List;

@Route(value = "ranking/all-time", layout = MainView.class)
@PageTitle("Global Ranking")
@AnonymousAllowed
public class RankingGlobalView extends VerticalLayout {
    public RankingGlobalView(LapService lapService) {

        Grid<Lap> grid = new Grid<>(Lap.class, true);

        List<Lap> laps = lapService.findAll();
        grid.setItems(laps);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        add(grid);
    }
}
