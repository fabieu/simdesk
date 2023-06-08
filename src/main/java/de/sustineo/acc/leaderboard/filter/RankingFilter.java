package de.sustineo.acc.leaderboard.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.acc.leaderboard.entities.Ranking;
import de.sustineo.acc.leaderboard.services.DriverService;
import lombok.Data;

@Data
public class RankingFilter {
    private final DriverService driverService;
    private final GridListDataView<Ranking> dataView;

    private String carGroup;
    private String trackName;
    private String driverName;
    private String carModelName;

    public RankingFilter(DriverService driverService, GridListDataView<Ranking> dataView) {
        this.driverService = driverService;
        this.dataView = dataView;
        this.dataView.addFilter(this::test);
    }

    public void setCarGroup(String carGroup) {
        this.carGroup = carGroup;
        this.dataView.refreshAll();
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
        this.dataView.refreshAll();
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
        this.dataView.refreshAll();
    }

    public void setCarModelName(String carModelName) {
        this.carModelName = carModelName;
        this.dataView.refreshAll();
    }

    public boolean test(Ranking ranking) {
        boolean matchesCarGroup = matches(ranking.getCarGroup().name(), carGroup);
        boolean matchesTrackName = matches(ranking.getTrackName(), trackName);
        boolean matchesDriverId = matches(driverService.getDriverNameByPlayerId(ranking.getDriverId()), driverName);
        boolean matchesCarModelName = matches(ranking.getCarModelName(), carModelName);

        return matchesCarGroup && matchesTrackName && matchesDriverId && matchesCarModelName;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}