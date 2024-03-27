package de.sustineo.simdesk.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.simdesk.entities.Lap;

public class InvalidLapPartNameGenerator implements SerializableFunction<Lap, String> {
    @Override
    public String apply(Lap lap) {
        if (lap == null || lap.isValid()) {
            return null;
        }

        return "invalid-lap";
    }
}
