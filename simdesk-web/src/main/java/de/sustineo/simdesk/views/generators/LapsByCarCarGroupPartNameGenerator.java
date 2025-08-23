package de.sustineo.simdesk.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.record.LapsByAccCar;

public class LapsByCarCarGroupPartNameGenerator extends CarGroupPartNameGenerator implements SerializableFunction<LapsByAccCar, String> {
    @Override
    public String apply(LapsByAccCar lapsByAccCar) {
        if (lapsByAccCar == null) {
            return null;
        }

        CarGroup carGroup = AccCar.getGroupById(lapsByAccCar.car().getId());
        return getCarGroupPartName(carGroup);
    }
}