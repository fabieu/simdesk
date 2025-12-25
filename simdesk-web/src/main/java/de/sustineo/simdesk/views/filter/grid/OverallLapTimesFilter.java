package de.sustineo.simdesk.views.filter.grid;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.RaceTrack;
import de.sustineo.simdesk.entities.RaceTracks;
import de.sustineo.simdesk.entities.Simulation;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.ranking.GroupRanking;

public class OverallLapTimesFilter extends GridFilter<GroupRanking> {
    private CarGroup carGroup;
    private RaceTrack raceTrack;
    private String driverName;
    private String carModelName;

    public OverallLapTimesFilter(GridListDataView<GroupRanking> dataView) {
        super(dataView);
    }

    public void setCarGroup(CarGroup carGroup) {
        this.carGroup = carGroup;
        refresh();
    }

    public void setRaceTrack(RaceTrack raceTrack) {
        this.raceTrack = raceTrack;
        refresh();
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
        refresh();
    }

    public void setCarModelName(String carModelName) {
        this.carModelName = carModelName;
        refresh();
    }

    protected boolean test(GroupRanking groupRanking) {
        boolean matchesCarGroup = matches(groupRanking.getCarGroup(), carGroup);
        boolean matchesRaceTrack = matches(RaceTracks.getById(Simulation.ACC, groupRanking.getTrackId()), raceTrack);
        boolean matchesDriverName = matches(groupRanking.getDriver().getFullName(), driverName);
        boolean matchesCarModelName = matches(AccCar.getModelById(groupRanking.getCarModelId()), carModelName);

        return matchesCarGroup && matchesRaceTrack && matchesDriverName && matchesCarModelName;
    }
}
