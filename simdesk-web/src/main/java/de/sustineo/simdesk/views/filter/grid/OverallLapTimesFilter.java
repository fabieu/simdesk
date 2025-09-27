package de.sustineo.simdesk.views.filter.grid;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.ranking.GroupRanking;

public class OverallLapTimesFilter extends GridFilter<GroupRanking> {
    private CarGroup carGroup;
    private String trackName;
    private String driverName;
    private String carModelName;

    public OverallLapTimesFilter(GridListDataView<GroupRanking> dataView) {
        super(dataView);
    }

    public void setCarGroup(CarGroup carGroup) {
        this.carGroup = carGroup;
        refresh();
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
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
        boolean matchesTrackName = matches(Track.getTrackNameByAccId(groupRanking.getTrackId()), trackName);
        boolean matchesDriverName = matches(groupRanking.getDriver().getFullName(), driverName);
        boolean matchesCarModelName = matches(AccCar.getModelById(groupRanking.getCarModelId()), carModelName);

        return matchesCarGroup && matchesTrackName && matchesDriverName && matchesCarModelName;
    }
}
