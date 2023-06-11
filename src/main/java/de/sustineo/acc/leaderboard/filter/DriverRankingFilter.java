package de.sustineo.acc.leaderboard.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.acc.leaderboard.entities.DriverRanking;
import de.sustineo.acc.leaderboard.services.DriverService;
import lombok.Data;

@Data
public class DriverRankingFilter {
    private final DriverService driverService;
    private final GridListDataView<DriverRanking> dataView;


    private String driverName;
    private String carModelName;
    private String sessionDescription;

    public DriverRankingFilter(DriverService driverService, GridListDataView<DriverRanking> dataView) {
        this.driverService = driverService;
        this.dataView = dataView;
        this.dataView.addFilter(this::test);
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
        this.dataView.refreshAll();
    }

    public void setCarModelName(String carModelName) {
        this.carModelName = carModelName;
        this.dataView.refreshAll();
    }

    public void setSessionDescription(String sessionDescription) {
        this.sessionDescription = sessionDescription;
        this.dataView.refreshAll();
    }

    public boolean test(DriverRanking driverRanking) {
        boolean matchesDriverId = matches(driverRanking.getDriver().getFullName(), driverName);
        boolean matchesCarModelName = matches(driverRanking.getCarModelName(), carModelName);
        boolean matchesSessionDescription = matches(driverRanking.getSessionDescription(), sessionDescription);

        return matchesDriverId && matchesCarModelName && matchesSessionDescription;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}