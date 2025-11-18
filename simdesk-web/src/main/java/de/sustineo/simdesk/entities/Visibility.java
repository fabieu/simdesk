package de.sustineo.simdesk.entities;

import java.util.EnumSet;
import java.util.Set;

public enum Visibility {
    PRIVATE,
    PUBLIC;

    public static Set<Visibility> getAll() {
        return EnumSet.allOf(Visibility.class);
    }
}