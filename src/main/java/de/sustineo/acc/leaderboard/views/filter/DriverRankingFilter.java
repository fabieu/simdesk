package de.sustineo.acc.leaderboard.views.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.acc.leaderboard.entities.ranking.DriverRanking;
import de.sustineo.acc.leaderboard.services.DriverService;
import lombok.Data;

@Data
public class DriverRankingFilter {
    private final DriverService driverService;
    private final GridListDataView<DriverRanking> dataView;


    private String driverName;
    private String carModelName;
    private String serverName;
    private String sessionTypeDescription;

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

    public void setServerName(String serverName) {
        this.serverName = serverName;
        this.dataView.refreshAll();
    }

    public void setSessionTypeDescription(String sessionTypeDescription) {
        this.sessionTypeDescription = sessionTypeDescription;
        this.dataView.refreshAll();
    }

    public boolean test(DriverRanking driverRanking) {
        boolean matchesDriverId = matches(driverRanking.getDriver().getEntireName(), driverName);
        boolean matchesCarModelName = matches(driverRanking.getCarModelName(), carModelName);
        boolean matchesServerName = matches(driverRanking.getSession().getServerName(), serverName);
        boolean matchesSessionTypeDescription = matches(driverRanking.getSession().getSessionType().getDescription(), sessionTypeDescription);

        return matchesDriverId && matchesCarModelName && matchesServerName && matchesSessionTypeDescription;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}