package de.sustineo.acc.servertools.services.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import de.sustineo.acc.servertools.entities.auth.UserPrincipal;
import de.sustineo.acc.servertools.entities.mapper.UserMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Log
@Service
public class SecurityService {
    private static final String LOGOUT_SUCCESS_URL = "/";

    @Value("${leaderboard.auth.admin.username}")
    private String adminUsername;

    @Value("${leaderboard.auth.admin.password}")
    private String adminPassword;

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public SecurityService(PasswordEncoder passwordEncoder,
                           UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    private void initializeAdminUser() {
        // Generate random password if not set
        if (adminPassword == null || adminPassword.isEmpty()) {
            adminPassword = UUID.randomUUID().toString();
            log.info(String.format("Please change the password via environment variable AUTH_ADMIN_PASSWORD \n\n Generated random admin password: %s \n", adminPassword));
        }

        userMapper.insert(adminUsername, passwordEncoder.encode(adminPassword));
    }

    public Optional<UserPrincipal> getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();

        if (principal instanceof UserPrincipal) {
            return Optional.of((UserPrincipal) principal);
        }

        if (principal instanceof DefaultOAuth2User) {
            return Optional.of(new UserPrincipal((DefaultOAuth2User) principal));
        }

        return Optional.empty(); // Anonymous or no authentication
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }
}
