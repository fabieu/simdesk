package de.sustineo.simdesk.views.generators;

import com.vaadin.flow.function.SerializableFunction;
import de.sustineo.simdesk.entities.Bop;

public class InactiveBopPartNameGenerator implements SerializableFunction<Bop, String> {
    @Override
    public String apply(Bop bop) {
        if (bop == null || bop.isActive()) {
            return null;
        }

        return "inactive-bop";
    }
}
