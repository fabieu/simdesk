package de.sustineo.simdesk.views.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.raceapp.RaceAppResult;
import lombok.Data;

@Data
public class RaceAppStandingsFilter {
    private final GridListDataView<RaceAppResult> dataView;

    private String teamName;
    private String driverNames;
    private String carGroup;
    private String carModel;

    public RaceAppStandingsFilter(GridListDataView<RaceAppResult> dataView) {
        this.dataView = dataView;
        this.dataView.addFilter(this::test);
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
        this.dataView.refreshAll();
    }

    public void setDriverNames(String driverNames) {
        this.driverNames = driverNames;
        this.dataView.refreshAll();
    }

    public void setCarGroup(String carGroup) {
        this.carGroup = carGroup;
        this.dataView.refreshAll();
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
        this.dataView.refreshAll();
    }

    public boolean test(RaceAppResult raceAppResult) {
        boolean matchesTeamName = matches(raceAppResult.getCarName(), teamName);
        boolean matchesDriverNames = matches(raceAppResult.getDriversString(), driverNames);
        boolean matchesCarGroup = matches(raceAppResult.getVehicleClass(), carGroup);
        boolean matchesCarModel = matches(raceAppResult.getVehicleModel(), carModel);

        return matchesTeamName && matchesDriverNames && matchesCarGroup && matchesCarModel;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
