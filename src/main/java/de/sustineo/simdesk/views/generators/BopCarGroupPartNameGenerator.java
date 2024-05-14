package de.sustineo.simdesk.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.simdesk.entities.Bop;
import de.sustineo.simdesk.entities.Car;
import de.sustineo.simdesk.entities.CarGroup;

public class BopCarGroupPartNameGenerator extends CarGroupPartNameGenerator implements SerializableFunction<Bop, String> {
    @Override
    public String apply(Bop bop) {
        if (bop == null || bop.getCarId() == null) {
            return null;
        }

        CarGroup carGroup = Car.getCarGroupById(bop.getCarId());
        return getCarGroupPartName(carGroup);
    }
}
