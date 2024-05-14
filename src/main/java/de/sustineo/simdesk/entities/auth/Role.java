package de.sustineo.simdesk.entities.auth;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ADMIN"),
    BOP_MANAGER("BOP-MANAGER");

    private final String definition;

    Role(String definition) {
        this.definition = definition;
    }
}
