package de.sustineo.acc.leaderboard.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.acc.leaderboard.entities.GroupRanking;
import de.sustineo.acc.leaderboard.services.DriverService;
import lombok.Data;

@Data
public class GroupRankingFilter {
    private final DriverService driverService;
    private final GridListDataView<GroupRanking> dataView;

    private String carGroup;
    private String trackName;
    private String driverName;
    private String carModelName;

    public GroupRankingFilter(DriverService driverService, GridListDataView<GroupRanking> dataView) {
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

    public boolean test(GroupRanking groupRanking) {
        boolean matchesCarGroup = matches(groupRanking.getCarGroup().name(), carGroup);
        boolean matchesTrackName = matches(groupRanking.getTrackName(), trackName);
        boolean matchesDriverId = matches(groupRanking.getDriver().getFullName(), driverName);
        boolean matchesCarModelName = matches(groupRanking.getCarModelName(), carModelName);

        return matchesCarGroup && matchesTrackName && matchesDriverId && matchesCarModelName;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}