package de.sustineo.simdesk.entities;

public enum SessionType {
    FP("Free Practice"),
    Q("Qualifying"),
    R("Race"),
    UNKNOWN("Unknown");

    private final String description;

    SessionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
