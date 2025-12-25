package de.sustineo.simdesk.views.filter.grid;

import com.vaadin.flow.component.grid.dataview.GridListDataView;
import de.sustineo.simdesk.entities.RaceTrack;
import de.sustineo.simdesk.entities.RaceTracks;
import de.sustineo.simdesk.entities.Simulation;
import de.sustineo.simdesk.entities.bop.Bop;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;

public class BopManagementFilter extends GridFilter<Bop> {
    private RaceTrack raceTrack;
    private String carModel;
    private String active;
    private String username;

    public BopManagementFilter(GridListDataView<Bop> dataView) {
        super(dataView);
    }

    public void setRaceTrack(RaceTrack raceTrack) {
        this.raceTrack = raceTrack;
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
        boolean matchesRaceTrack = matches(RaceTracks.getById(Simulation.ACC, bop.getTrackId()), raceTrack);
        boolean matchesCarModel = matches(AccCar.getModelById(bop.getCarId()), carModel);
        boolean matchesActive = matches(String.valueOf(bop.getActive()), active);
        boolean matchesUsername = matches(bop.getUsername(), username);

        return matchesRaceTrack && matchesCarModel && matchesActive && matchesUsername;
    }
}
