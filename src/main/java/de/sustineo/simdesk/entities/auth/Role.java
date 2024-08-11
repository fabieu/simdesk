package de.sustineo.simdesk.entities.auth;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ADMIN");

    private final String definition;

    Role(String definition) {
        this.definition = definition;
    }
}
