package de.sustineo.simdesk.entities.menu;

import lombok.Getter;

@Getter
public enum MenuEntityCategory {
    LEADERBOARD("Leaderboard"),
    BALANCE_OF_PERFORMANCE("Balance of Performance"),
    ENTRYLIST("Entrylist"),
    MAP("Map");

    private final String name;

    MenuEntityCategory(String name) {
        this.name = name;
    }
}
