package de.sustineo.simdesk.services.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import de.sustineo.simdesk.configuration.SecurityConfiguration;
import de.sustineo.simdesk.entities.auth.ApiKey;
import de.sustineo.simdesk.entities.auth.UserPrincipal;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Log
@Service
public class SecurityService {
    private static final String LOGOUT_SUCCESS_URL = "/";
    public static final String DISCORD_CDN_AVATARS_URL = "https://cdn.discordapp.com/avatars";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${simdesk.auth.admin.username}")
    private String adminUsername;
    @Value("${simdesk.auth.admin.password}")
    private String adminPassword;

    public SecurityService(UserService userService,
                           PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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

    public Optional<UserPrincipal> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = Optional.ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .orElse(null);

        if (principal instanceof UserPrincipal) {
            return Optional.of((UserPrincipal) principal);
        }

        if (principal instanceof DefaultOAuth2User) {
            return Optional.of(new UserPrincipal((DefaultOAuth2User) principal));
        }

        return Optional.empty(); // Anonymous or no authentication
    }

    public Optional<ApiKey> getAuthenticatedApiKey() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getDetails() == null) {
            return Optional.empty();
        }

        Object authenticationDetails = authentication.getDetails();
        if (authenticationDetails instanceof ApiKey) {
            return Optional.of((ApiKey) authenticationDetails);
        }

        return Optional.empty();
    }

    /**
     * Get the username of the currently authenticated user
     *
     * @return the username or null if not authenticated
     */
    public String getAuthenticatedUsername() {
        return getAuthenticatedUser()
                .map(UserPrincipal::getUsername)
                .orElse(null);
    }

    public boolean hasAnyAuthority(UserRoleEnum... roles) {
        Optional<UserPrincipal> user = getAuthenticatedUser();
        Optional<ApiKey> apiKey = getAuthenticatedApiKey();

        if (user.isPresent()) {
            for (GrantedAuthority grantedAuthority : user.get().getAuthorities()) {
                for (UserRoleEnum role : roles) {
                    if (grantedAuthority.getAuthority().equals(role.name())) {
                        return true;
                    }
                }
            }
        }

        if (apiKey.isPresent()) {
            for (UserRoleEnum role : roles) {
                if (apiKey.get().getRoles().contains(role)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }


    public Optional<String> getAvatarUrl() {
        Optional<UserPrincipal> user = getAuthenticatedUser();

        if (user.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> userAttributes = user.get().getAttributes();

        // Discord avatar
        String avatarId = (String) userAttributes.get("avatar");
        String userId = (String) userAttributes.get("id");

        if (avatarId == null || userId == null) {
            return Optional.empty();
        }

        return Optional.of(DISCORD_CDN_AVATARS_URL + "/" + userId + "/" + avatarId);
    }
}
