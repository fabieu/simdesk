package de.sustineo.simdesk.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Simulation {
    ACC("acc", "Assetto Corsa Competizione"),
    LMU("lmu", "Le Mans Ultimate");

    private final String id;
    private final String displayName;

    public static Simulation getById(String id) {
        if (id == null) {
            return null;
        }

        for (Simulation simulation : values()) {
            if (simulation.getId().equals(id)) {
                return simulation;
            }
        }

        return null;
    }
}
