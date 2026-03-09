package de.sustineo.simdesk.entities.stewarding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PenaltySessionType {
    PRACTICE("Practice"),
    QUALIFYING("Qualifying"),
    RACE("Race"),
    ALL("All");

    private final String description;
}
