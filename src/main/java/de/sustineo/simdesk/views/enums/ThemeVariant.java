package de.sustineo.simdesk.views.enums;

import lombok.Getter;

@Getter
public enum ThemeVariant {
    BLACK_AND_WHITE,
    DEFAULT,
    MODERN_GREEN,
    PPR,
    RACING_RED,
    SIM2REAL;

    public String getAttribute() {
        return name().toLowerCase();
    }
}
