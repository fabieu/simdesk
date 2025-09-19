package de.sustineo.simdesk.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.simdesk.entities.bop.Bop;

public class InactiveBopPartNameGenerator implements SerializableFunction<Bop, String> {
    @Override
    public String apply(Bop bop) {
        if (bop == null || bop.getActive()) {
            return null;
        }

        return "inactive-bop";
    }
}
