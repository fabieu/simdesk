package de.sustineo.simdesk.entities.auth;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPermission {
    private Integer userId;
    private UserRoleEnum role;
    private Instant insertDatetime;
}
