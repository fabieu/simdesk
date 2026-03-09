package de.sustineo.simdesk.entities.stewarding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StewSessionType {
    PRACTICE("Practice"),
    QUALIFYING("Qualifying"),
    RACE("Race");

    private final String description;
}
