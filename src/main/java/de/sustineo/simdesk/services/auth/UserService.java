package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.entities.auth.User;
import de.sustineo.simdesk.entities.auth.UserPermission;
import de.sustineo.simdesk.entities.auth.UserRole;
import de.sustineo.simdesk.mapper.UserMapper;
import de.sustineo.simdesk.mapper.UserPermissionMapper;
import de.sustineo.simdesk.mapper.UserRoleMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserPermissionMapper userPermissionMapper;
    private final UserRoleMapper userRoleMapper;

    public UserService(UserMapper userMapper,
                       UserPermissionMapper userPermissionMapper,
                       UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.userPermissionMapper = userPermissionMapper;
        this.userRoleMapper = userRoleMapper;
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public void insertSystemUser(String username, String password, Long userId) {
        User user = User.builder()
                .userId(userId)
                .username(username)
                .password(password)
                .build();

        userMapper.insertSystemUser(user);
    }

    public Set<? extends GrantedAuthority> getAuthoritiesByUserId(Long userId) {
        List<UserPermission> userPermissions = userPermissionMapper.findByUserId(userId);

        return userPermissions.stream()
                .map(userPermission -> new SimpleGrantedAuthority(userPermission.getRoleName()))
                .collect(Collectors.toSet());
    }

    public List<UserRole> getAllRoles() {
        return userRoleMapper.findAll();
    }

    @PreAuthorize("hasAuthority(T(de.sustineo.simdesk.entities.auth.UserRole).ADMIN)")
    public void updateUserRole(UserRole userRole) {
        userRoleMapper.update(userRole);
    }
}
