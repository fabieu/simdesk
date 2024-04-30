package de.sustineo.simdesk.entities.menu;

import lombok.Getter;

@Getter
public enum MenuEntityCategory {
    MAIN("Main"),
    LEADERBOARD("Leaderboard"),
    BALANCE_OF_PERFORMANCE("Balance of Performance"),
    ENTRYLIST("Entrylist"),
    PERMIT("Permit"),
    EXTERNAL_LINKS("External Links");

    private final String name;

    MenuEntityCategory(String name) {
        this.name = name;
    }
}
