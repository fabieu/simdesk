package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.configuration.SecurityConfiguration;
import de.sustineo.simdesk.entities.auth.User;
import de.sustineo.simdesk.entities.auth.UserPrincipal;
import de.sustineo.simdesk.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public DatabaseUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("id", String.valueOf(user.getUserId()));
        attributes.put(SecurityConfiguration.ATTRIBUTE_AUTH_PROVIDER, SecurityConfiguration.AUTH_PROVIDER_DATABASE);

        user.setAttributes(Collections.unmodifiableMap(attributes));
        user.setAuthorities(Collections.emptySet());

        return new UserPrincipal(user);
    }
}
