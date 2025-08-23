package de.sustineo.simdesk.entities;

import lombok.Getter;

@Getter
public enum SortingDirection {
    ASC("Ascending"),
    DESC("Descending");

    private final String label;

    SortingDirection(String label) {
        this.label = label;
    }
}
