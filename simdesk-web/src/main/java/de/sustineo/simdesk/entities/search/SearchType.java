package de.sustineo.simdesk.entities.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {
    DRIVER("Driver"),
    SESSION("Session");

    private final String label;
}
