package de.sustineo.simdesk.entities;

import lombok.Getter;

@Getter
public enum Simulation {
    ACC("Assetto Corsa Competizione", "ACC", true),
    AC("Assetto Corsa", "AC", false),
    ACE("Assetto Corsa Evo", "ACEvo", false);

    private final String name;
    private final String shortName;
    private final boolean active;

    Simulation(String name, String shortName, boolean active) {
        this.name = name;
        this.shortName = shortName;
        this.active = active;
    }
}
