package de.sustineo.simdesk.entities.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRole {
    public static final String ADMIN = "ROLE_ADMIN";

    @NotNull
    private String name;
    private String description;
    private String discordRoleId;

    /**
     * The Grid editor needs to know what has changed in order to close the right thing.
     * Make sure that equals and hashCode of <code>UserRole</code> uses unique attributes.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole userRole)) return false;

        return name.equals(userRole.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
