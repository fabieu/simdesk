package de.sustineo.simdesk.services;

import de.sustineo.simdesk.entities.auth.DiscordUser;
import de.sustineo.simdesk.entities.auth.User;
import de.sustineo.simdesk.entities.mapper.DiscordUserMapper;
import de.sustineo.simdesk.entities.mapper.UserMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final DiscordUserMapper discordUserMapper;

    public static final Long USER_ID_DEFAULT_ADMIN = 10000L;

    public UserService(UserMapper userMapper,
                       DiscordUserMapper discordUserMapper) {
        this.userMapper = userMapper;
        this.discordUserMapper = discordUserMapper;
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public void insertSystemUser(String username, String password, Long userId) {
        User user = User.builder()
                .userId(userId)
                .username(username)
                .password(password)
                .updateDatetime(Instant.now())
                .build();

        userMapper.insertSystemUser(user);
    }

    @Cacheable(value = "discord_users", key = "#userId")
    public DiscordUser findDiscordUserByUserId(Long userId) {
        return discordUserMapper.findByUserId(userId);
    }

    @CacheEvict(value = "discord_users", key = "#user.userId")
    public void insertDiscordUser(DiscordUser user) {
        discordUserMapper.insert(user);
    }
}
