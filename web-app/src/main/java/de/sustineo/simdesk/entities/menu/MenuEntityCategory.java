package de.sustineo.simdesk.entities.menu;

import lombok.Getter;

@Getter
public enum MenuEntityCategory {
    LEADERBOARD("Leaderboard"),
    LIVE_TIMING("Live Timing"),
    ENTRYLIST("Entrylist"),
    BALANCE_OF_PERFORMANCE("Balance of Performance"),
    MAP("Map");

    private final String name;

    MenuEntityCategory(String name) {
        this.name = name;
    }
}
