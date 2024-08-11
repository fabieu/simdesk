package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.entities.auth.UserRole;
import de.sustineo.simdesk.entities.mapper.UserRoleMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserRoleService {
    private final UserRoleMapper userRoleMapper;

    public UserRoleService(UserRoleMapper userRoleMapper) {
        this.userRoleMapper = userRoleMapper;
    }

    public Set<? extends GrantedAuthority> findAuthoritiesByUserId(Long userId) {
        List<UserRole> userRoles = userRoleMapper.findByUserId(userId);

        return userRoles.stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRoleName()))
                .collect(Collectors.toSet());
    }
}
