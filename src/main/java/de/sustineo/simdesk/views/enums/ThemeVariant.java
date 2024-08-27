package de.sustineo.simdesk.views.enums;

import lombok.Getter;

@Getter
public enum ThemeVariant {
    DEFAULT, SIM2REAL;

    public String getAttribute() {
        return name().toLowerCase();
    }
}
