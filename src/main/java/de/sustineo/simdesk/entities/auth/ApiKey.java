package de.sustineo.simdesk.entities.auth;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ApiKey {
    private Integer id;
    private User user;
    private String apiKey;
    private String name;
    private Boolean active;
    private List<UserRoleEnum> roles;
    private Instant creationDatetime;
}
