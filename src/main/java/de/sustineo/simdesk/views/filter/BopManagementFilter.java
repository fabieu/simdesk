package de.sustineo.simdesk.views.filter;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.Bop;
import de.sustineo.simdesk.entities.Car;
import de.sustineo.simdesk.entities.Track;

public class BopManagementFilter extends GridFilter {
    private final GridListDataView<Bop> dataView;

    private String trackName;
    private String carName;
    private String active;
    private String username;

    public BopManagementFilter(GridListDataView<Bop> dataView) {
        this.dataView = dataView;
        this.dataView.addFilter(this::test);
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
        this.dataView.refreshAll();
    }

    public void setCarName(String carName) {
        this.carName = carName;
        this.dataView.refreshAll();
    }

    public void setActive(String active) {
        this.active = active;
        this.dataView.refreshAll();
    }

    public void setUsername(String username) {
        this.username = username;
        this.dataView.refreshAll();
    }

    private boolean test(Bop bop) {
        boolean matchesTrackName = matches(Track.getTrackNameByAccId(bop.getTrackId()), trackName);
        boolean matchesCarName = matches(Car.getCarNameById(bop.getCarId()), carName);
        boolean matchesActive = matches(String.valueOf(bop.getActive()), active);
        boolean matchesUsername = matches(bop.getUsername(), username);

        return matchesTrackName && matchesCarName && matchesActive && matchesUsername;
    }
}
