package de.sustineo.simdesk.entities.auth;

import lombok.Data;

import java.time.Instant;

@Data
public class UserPermission {
    private Integer userId;
    private UserRoleEnum role;
    private Instant insertDatetime;
}
