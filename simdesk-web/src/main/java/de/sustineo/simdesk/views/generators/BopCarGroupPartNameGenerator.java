package de.sustineo.simdesk.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.bop.Bop;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;

public class BopCarGroupPartNameGenerator extends CarGroupPartNameGenerator implements SerializableFunction<Bop, String> {
    @Override
    public String apply(Bop bop) {
        if (bop == null || bop.getCarId() == null) {
            return null;
        }

        CarGroup carGroup = AccCar.getGroupById(bop.getCarId());
        return getCarGroupPartName(carGroup);
    }
}
