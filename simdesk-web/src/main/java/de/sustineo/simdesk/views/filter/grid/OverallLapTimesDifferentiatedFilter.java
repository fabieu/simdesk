package de.sustineo.simdesk.views.filter.grid;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.ranking.DriverRanking;

public class OverallLapTimesDifferentiatedFilter extends GridFilter<DriverRanking> {
    private String driverName;
    private String carModelName;

    public OverallLapTimesDifferentiatedFilter(GridListDataView<DriverRanking> dataView) {
        super(dataView);
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
        refresh();
    }

    public void setCarModelName(String carModelName) {
        this.carModelName = carModelName;
        refresh();
    }

    protected boolean test(DriverRanking driverRanking) {
        boolean matchesDriverFullName = matches(driverRanking.getDriver().getFullName(), driverName);
        boolean matchesCarModelName = matches(AccCar.getModelById(driverRanking.getCarModelId()), carModelName);

        return matchesDriverFullName && matchesCarModelName;
    }
}