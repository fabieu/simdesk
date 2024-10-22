package de.sustineo.simdesk.entities;

import lombok.Getter;

@Getter
public enum Simulation {
    ACC("Assetto Corsa Competizione", true),
    AC("Assetto Corsa", false),
    ACE("Assetto Corsa Evo", false);

    private final String name;
    private final boolean active;

    Simulation(String name, boolean active) {
        this.name = name;
        this.active = active;
    }
}
