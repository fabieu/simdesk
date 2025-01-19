package de.sustineo.simdesk.entities.auth;

import lombok.Data;

import java.util.List;

@Data
public class ApiKey {
    private Integer id;
    private Integer userId;
    private String apiKey;
    private String name;
    private Boolean active;
    private transient List<UserRoleEnum> roles;
}
