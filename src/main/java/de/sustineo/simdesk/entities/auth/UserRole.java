package de.sustineo.simdesk.entities.auth;

import lombok.Data;

@Data
public class UserRole {
    public static final String ADMIN = "ROLE_ADMIN";

    private String name;
    private String description;
    private Long discordRoleId;
}
