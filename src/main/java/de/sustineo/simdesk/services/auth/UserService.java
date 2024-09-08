package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.entities.auth.User;
import de.sustineo.simdesk.entities.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
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
}
