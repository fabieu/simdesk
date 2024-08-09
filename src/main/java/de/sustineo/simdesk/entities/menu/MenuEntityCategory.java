package de.sustineo.simdesk.entities.menu;

import lombok.Getter;

@Getter
public enum MenuEntityCategory {
    MAIN("Main"),
    LEADERBOARD("Leaderboard"),
    BALANCE_OF_PERFORMANCE("Balance of Performance"),
    TOOLS("Tools");

    private final String name;

    MenuEntityCategory(String name) {
        this.name = name;
    }
}
