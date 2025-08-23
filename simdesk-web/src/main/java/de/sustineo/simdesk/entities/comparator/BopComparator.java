package de.sustineo.simdesk.entities.comparator;

import de.sustineo.simdesk.entities.Bop;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BopComparator implements Comparator<Bop> {
    private final List<Comparator<Bop>> comparatorList = new ArrayList<>();

    public BopComparator() {
        comparatorList.add(Comparator.comparing(Bop::getTrackId));
        comparatorList.add(Comparator.comparing(bop -> AccCar.getGroupById(bop.getCarId())));
        comparatorList.add(Comparator.comparing(bop -> AccCar.getModelById(bop.getCarId())));
    }

    @Override
    public int compare(Bop b1, Bop b2) {
        int result;
        for (Comparator<Bop> comparator : comparatorList) {
            if ((result = comparator.compare(b1, b2)) != 0) {
                return result;
            }
        }
        return 0;
    }
}
