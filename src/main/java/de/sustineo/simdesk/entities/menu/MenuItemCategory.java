package de.sustineo.simdesk.entities.menu;

import lombok.Getter;

@Getter
public enum MenuItemCategory {
    MAIN("Main"),
    LEADERBOARD("Leaderboard"),
    BALANCE_OF_PERFORMANCE("Balance of Performance"),
    ENTRYLIST("Entrylist"),
    EXTERNAL_LINKS("External Links");

    private final String name;

    MenuItemCategory(String name) {
        this.name = name;
    }
}
