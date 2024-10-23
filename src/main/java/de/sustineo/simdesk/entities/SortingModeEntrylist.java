package de.sustineo.simdesk.entities;

import lombok.Getter;

@Getter
public enum SortingModeEntrylist {
    NONE("None"),
    GRID_POSITION("Grid Position"),
    CAR_NUMBER("Car Number"),
    ADMIN("Admin");

    private final String label;

    SortingModeEntrylist(String label) {
        this.label = label;
    }
}
