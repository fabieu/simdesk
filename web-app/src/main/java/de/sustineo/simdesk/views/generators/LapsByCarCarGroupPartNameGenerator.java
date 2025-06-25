package de.sustineo.simdesk.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.simdesk.entities.Car;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.record.LapsByCar;

public class LapsByCarCarGroupPartNameGenerator extends CarGroupPartNameGenerator implements SerializableFunction<LapsByCar, String> {
    @Override
    public String apply(LapsByCar lapsByCar) {
        if (lapsByCar == null || lapsByCar.car().getModelId() == null) {
            return null;
        }

        CarGroup carGroup = Car.getGroupById(lapsByCar.car().getModelId());
        return getCarGroupPartName(carGroup);
    }
}