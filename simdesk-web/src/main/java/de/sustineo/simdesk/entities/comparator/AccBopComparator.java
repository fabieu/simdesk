package de.sustineo.simdesk.entities.comparator;

import de.sustineo.simdesk.entities.RaceTracks;
import de.sustineo.simdesk.entities.Simulation;
import de.sustineo.simdesk.entities.bop.Bop;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;

import java.util.Comparator;

public class AccBopComparator implements Comparator<Bop> {
    @Override
    public int compare(Bop b1, Bop b2) {
        return Comparator
                .comparing((Bop bop) -> RaceTracks.getById(Simulation.ACC, bop.getTrackId()).getDisplayName())
                .thenComparing(bop -> AccCar.getGroupById(bop.getCarId()))
                .thenComparing(bop -> AccCar.getModelById(bop.getCarId()))
                .compare(b1, b2);
    }
}
