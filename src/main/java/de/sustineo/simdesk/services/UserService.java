package de.sustineo.simdesk.services;

import de.sustineo.simdesk.entities.auth.DiscordUser;
import de.sustineo.simdesk.entities.auth.User;
import de.sustineo.simdesk.entities.mapper.DiscordUserMapper;
import de.sustineo.simdesk.entities.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final DiscordUserMapper discordUserMapper;

    public UserService(UserMapper userMapper,
                       DiscordUserMapper discordUserMapper) {
        this.userMapper = userMapper;
        this.discordUserMapper = discordUserMapper;
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public void insertUser(String username, String password) {
        User user = User.builder()
                .username(username)
                .password(password)
                .updateDatetime(Instant.now())
                .build();

        userMapper.insert(user);
    }

    public void insertDiscordUser(DiscordUser user) {
        discordUserMapper.insert(user);
    }
}
