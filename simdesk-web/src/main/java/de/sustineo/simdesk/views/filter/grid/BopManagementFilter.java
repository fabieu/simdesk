package de.sustineo.simdesk.views.filter.grid;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.Track;
import de.sustineo.simdesk.entities.bop.Bop;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;

public class BopManagementFilter extends GridFilter<Bop> {
    private String trackName;
    private String carModel;
    private String active;
    private String username;

    public BopManagementFilter(GridListDataView<Bop> dataView) {
        super(dataView);
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
        refresh();
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
        refresh();
    }

    public void setActive(String active) {
        this.active = active;
        refresh();
    }

    public void setUsername(String username) {
        this.username = username;
        refresh();
    }

    protected boolean test(Bop bop) {
        boolean matchesTrackName = matches(Track.getTrackNameByAccId(bop.getTrackId()), trackName);
        boolean matchesCarModel = matches(AccCar.getModelById(bop.getCarId()), carModel);
        boolean matchesActive = matches(String.valueOf(bop.getActive()), active);
        boolean matchesUsername = matches(bop.getUsername(), username);

        return matchesTrackName && matchesCarModel && matchesActive && matchesUsername;
    }
}
