package de.sustineo.acc.servertools.entities.auth;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
}
