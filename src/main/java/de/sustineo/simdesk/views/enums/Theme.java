package de.sustineo.simdesk.views.enums;

import lombok.Getter;

@Getter
public enum Theme {
    DEFAULT("default-theme"),
    SIM2REAL("sim2real-theme");

    private final String themeCssClass;

    Theme(String themeCssClass) {
        this.themeCssClass = themeCssClass;
    }
}
