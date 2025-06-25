package de.sustineo.simdesk.views.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.Car;
import de.sustineo.simdesk.entities.ranking.DriverRanking;

public class OverallLapTimesDifferentiatedFilter extends GridFilter {
    private final GridListDataView<DriverRanking> dataView;

    private String driverName;
    private String carModelName;
    private String sessionDescription;

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

    public void setSessionDescription(String sessionTypeDescription) {
        this.sessionDescription = sessionTypeDescription;
        this.dataView.refreshAll();
    }

    public boolean test(DriverRanking driverRanking) {
        boolean matchesDriverFullName = matches(driverRanking.getDriver().getFullName(), driverName);
        boolean matchesCarModelName = matches(Car.getNameById(driverRanking.getCarModelId()), carModelName);
        boolean matchesSessionDescription = matches(driverRanking.getSession().getDescription(), sessionDescription);

        return matchesDriverFullName && matchesCarModelName && matchesSessionDescription;
    }
}