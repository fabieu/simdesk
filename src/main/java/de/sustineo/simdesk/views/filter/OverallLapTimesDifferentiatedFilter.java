package de.sustineo.simdesk.views.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.ranking.DriverRanking;
import lombok.Data;

@Data
public class OverallLapTimesDifferentiatedFilter {
    private final GridListDataView<DriverRanking> dataView;

    private String driverName;
    private String carModelName;
    private String serverName;
    private String sessionTypeDescription;

    public OverallLapTimesDifferentiatedFilter(GridListDataView<DriverRanking> dataView) {
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
        boolean matchesDriverFullName = matches(driverRanking.getDriver().getFullName(), driverName);
        boolean matchesCarModelName = matches(driverRanking.getCarModelName(), carModelName);
        boolean matchesServerName = matches(driverRanking.getSession().getServerName(), serverName);
        boolean matchesSessionTypeDescription = matches(driverRanking.getSession().getSessionType().getDescription(), sessionTypeDescription);

        return matchesDriverFullName && matchesCarModelName && matchesServerName && matchesSessionTypeDescription;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}