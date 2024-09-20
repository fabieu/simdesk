package de.sustineo.simdesk.entities.auth;

import lombok.Data;

import java.time.Instant;

@Data
public class UserPermission {
    private Long userId;
    private String roleName;
    private Instant insertDatetime;
}
