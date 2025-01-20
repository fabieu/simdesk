package de.sustineo.simdesk.services.auth;

import com.vaadin.flow.spring.security.AuthenticationContext;
import de.sustineo.simdesk.configuration.SecurityConfiguration;
import de.sustineo.simdesk.entities.auth.UserPrincipal;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Log
@Service
public class SecurityService {
    public static final String DISCORD_CDN_AVATARS_URL = "https://cdn.discordapp.com/avatars";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final transient AuthenticationContext authenticationContext;

    @Value("${simdesk.auth.admin.username}")
    private String adminUsername;
    @Value("${simdesk.auth.admin.password}")
    private String adminPassword;

    public SecurityService(UserService userService,
                           PasswordEncoder passwordEncoder,
                           AuthenticationContext authenticationContext) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationContext = authenticationContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    private void initializeSystemUsers() {
        // Generate random password if not set
        if (adminPassword == null || adminPassword.isEmpty()) {
            adminPassword = UUID.randomUUID().toString();
            log.info(String.format("Please change the password via environment variable SIMDESK_ADMIN_PASSWORD \n\n Generated random admin password: %s \n", adminPassword));
        }

        userService.insertSystemUser(SecurityConfiguration.USER_ID_ADMIN, adminUsername, passwordEncoder.encode(adminPassword));
    }

    private static Optional<Authentication> getAuthentication() {
        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(auth -> !(auth instanceof AnonymousAuthenticationToken));
    }

    public Optional<UserPrincipal> getAuthenticatedUserPrincipal() {
        Object principal = getAuthentication()
                .map(Authentication::getPrincipal)
                .orElse(null);

        if (principal instanceof UserPrincipal) {
            return Optional.of((UserPrincipal) principal);
        }

        if (principal instanceof DefaultOAuth2User) {
            return Optional.of(new UserPrincipal((DefaultOAuth2User) principal));
        }

        return Optional.empty();
    }

    public Optional<String> getPrincipalName() {
        return authenticationContext.getPrincipalName();
    }

    public boolean hasAnyAuthority(UserRoleEnum... userRoles) {
        String[] authorities = Arrays.stream(userRoles)
                .map(UserRoleEnum::name)
                .toArray(String[]::new);

        return authenticationContext.hasAnyAuthority(authorities);
    }

    public void logout() {
        authenticationContext.logout();
    }

    public Optional<String> getAvatarUrl() {
        Optional<UserPrincipal> userPrincipal = getAuthenticatedUserPrincipal();
        if (userPrincipal.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> userAttributes = userPrincipal.get().getAttributes();
        String avatarId = (String) userAttributes.get("avatar");
        String userId = (String) userAttributes.get("id");

        if (avatarId == null || userId == null) {
            return Optional.empty();
        }

        return Optional.of(DISCORD_CDN_AVATARS_URL + "/" + userId + "/" + avatarId);
    }
}
