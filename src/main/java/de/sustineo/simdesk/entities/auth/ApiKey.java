package de.sustineo.simdesk.entities.auth;

import lombok.Data;

import java.util.List;

@Data
public class ApiKey {
    private Integer id;
    private String apiKey;
    private String name;
    private Boolean active;
    private User user;
    private List<UserRoleEnum> roles;
}
