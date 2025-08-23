package de.sustineo.simdesk.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SessionType {
    FP("Free Practice"),
    Q("Qualifying"),
    R("Race"),
    UNKNOWN("Unknown");

    private final String description;
}
