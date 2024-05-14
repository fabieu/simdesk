package de.sustineo.simdesk.views.generators;

import de.sustineo.simdesk.entities.CarGroup;

public class CarGroupPartNameGenerator {
    protected String getCarGroupPartName(CarGroup carGroup) {
        return "car-group-" + carGroup.name().toLowerCase();
    }
}
