package de.sustineo.simdesk.entities.entrylist;

import lombok.Getter;

@Getter
public enum EntrylistSortingMode {
    NONE("None"),
    GRID_POSITION("Grid Position"),
    CAR_NUMBER("Car Number"),
    ADMIN("Admin");

    private final String label;

    EntrylistSortingMode(String label) {
        this.label = label;
    }
}
