package de.sustineo.simdesk.entities.auth;

import lombok.Data;

@Data
public class UserRole {
    private String roleId;
    private String userId;
    private String roleName;
    private String insertDatetime;
}
