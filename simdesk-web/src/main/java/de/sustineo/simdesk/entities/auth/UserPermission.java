package de.sustineo.simdesk.entities.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPermission {
    private Integer userId;
    private UserRoleEnum role;

    public static UserPermission of(Integer userId, String role) {
        return of(userId, Enum.valueOf(UserRoleEnum.class, role));
    }

    public static UserPermission of(Integer userId, UserRoleEnum role) {
        return new UserPermission(userId, role);
    }
}
