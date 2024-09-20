package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.entities.auth.UserPermission;
import de.sustineo.simdesk.entities.mapper.UserPermissionMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserPermissionService {
    private final UserPermissionMapper userPermissionMapper;

    public UserPermissionService(UserPermissionMapper userPermissionMapper) {
        this.userPermissionMapper = userPermissionMapper;
    }

    public Set<? extends GrantedAuthority> getAuthoritiesByUserId(Long userId) {
        List<UserPermission> userPermissions = userPermissionMapper.findByUserId(userId);

        return userPermissions.stream()
                .map(userPermission -> new SimpleGrantedAuthority(userPermission.getRoleName()))
                .collect(Collectors.toSet());
    }
}
