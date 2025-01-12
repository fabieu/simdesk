package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.entities.auth.User;
import de.sustineo.simdesk.entities.auth.UserPermission;
import de.sustineo.simdesk.entities.auth.UserRole;
import de.sustineo.simdesk.repositories.UserPermissionRepository;
import de.sustineo.simdesk.repositories.UserRepository;
import de.sustineo.simdesk.repositories.UserRoleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final UserRoleRepository userRoleRepository;

    public UserService(UserRepository userRepository,
                       UserPermissionRepository userPermissionRepository,
                       UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updateSystemUser(Long userId, String username, String password) {
        User user = User.builder()
                .userId(userId)
                .username(username)
                .password(password)
                .build();

        userRepository.save(user);
    }

    public Set<? extends GrantedAuthority> getAuthoritiesByUserId(Long userId) {
        List<UserPermission> userPermissions = userPermissionRepository.findByUserId(userId);

        return userPermissions.stream()
                .map(userPermission -> new SimpleGrantedAuthority(userPermission.getRoleName()))
                .collect(Collectors.toSet());
    }

    public List<UserRole> getAllRoles() {
        return userRoleRepository.findAll();
    }

    @PreAuthorize("hasAuthority(T(de.sustineo.simdesk.entities.auth.UserRole).ADMIN)")
    public void updateUserRole(UserRole userRole) {
        userRoleRepository.update(userRole);
    }
}
