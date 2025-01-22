package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.entities.auth.*;
import de.sustineo.simdesk.mapper.UserMapper;
import de.sustineo.simdesk.mapper.UserPermissionMapper;
import de.sustineo.simdesk.mapper.UserRoleMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserPermissionMapper userPermissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final ApiKeyService apiKeyService;

    public UserService(UserMapper userMapper,
                       UserPermissionMapper userPermissionMapper,
                       UserRoleMapper userRoleMapper,
                       ApiKeyService apiKeyService) {
        this.userMapper = userMapper;
        this.userPermissionMapper = userPermissionMapper;
        this.userRoleMapper = userRoleMapper;
        this.apiKeyService = apiKeyService;
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public void insertSystemUser(Integer userId, String username, String password) {
        userMapper.insertSystemUser(userId, username, password, UserType.SYSTEM);
    }

    @Transactional
    public void insertDiscordUser(String discordUserId, Collection<? extends GrantedAuthority> authorities) {
        User user = userMapper.findByUsername(discordUserId);

        // If the user does not exist, create a new user and insert it into the database
        if (user == null) {
            user = User.builder()
                    .username(discordUserId)
                    .type(UserType.DISCORD)
                    .build();

            userMapper.insertDiscordUser(user);
        }

        // Refresh the roles of the user
        userPermissionMapper.deleteAllByUserId(user.getId());
        for (GrantedAuthority authority : authorities) {
            UserPermission userPermission = UserPermission.builder()
                    .userId(user.getId())
                    .role(UserRoleEnum.valueOf(authority.getAuthority()))
                    .build();

            userPermissionMapper.insert(userPermission);
        }

        apiKeyService.removeActiveApiKeysFromCache(user.getId());
    }

    public Set<? extends GrantedAuthority> getAuthoritiesByUserId(Integer userId) {
        List<UserPermission> userPermissions = userPermissionMapper.findByUserId(userId);

        return userPermissions.stream()
                .map(userPermission -> new SimpleGrantedAuthority(userPermission.getRole().name()))
                .collect(Collectors.toSet());
    }

    public List<UserRole> getAllRoles() {
        return userRoleMapper.findAll();
    }

    @PreAuthorize("hasAuthority(T(de.sustineo.simdesk.entities.auth.UserRoleEnum).ROLE_ADMIN)")
    public void updateUserRole(UserRole userRole) {
        userRoleMapper.update(userRole);
    }
}
