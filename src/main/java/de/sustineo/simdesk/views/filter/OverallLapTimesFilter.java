package de.sustineo.simdesk.views.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.ranking.GroupRanking;

public class OverallLapTimesFilter extends GridFilter {
    private final GridListDataView<GroupRanking> dataView;

    private String carGroup;
    private String trackName;
    private String driverName;
    private String carModelName;

    public OverallLapTimesFilter(GridListDataView<GroupRanking> dataView) {
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
        boolean matchesDriverName = matches(groupRanking.getDriver().getFullNameCensored(), driverName);
        boolean matchesCarModelName = matches(groupRanking.getCarModelName(), carModelName);

        return matchesCarGroup && matchesTrackName && matchesDriverName && matchesCarModelName;
    }
}
