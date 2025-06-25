package de.sustineo.simdesk.entities.auth;

import lombok.*;

@Data
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    private Integer id;
    private String username;
    private String password;
    private UserType type;

    public static User ofSystem(Integer id, String username, String password) {
        return User.builder()
                .type(UserType.SYSTEM)
                .id(id)
                .username(username)
                .password(password)
                .build();
    }

    public static User ofDiscord(String discordUserId) {
        return User.builder()
                .type(UserType.DISCORD)
                .username(discordUserId)
                .build();
    }
}
