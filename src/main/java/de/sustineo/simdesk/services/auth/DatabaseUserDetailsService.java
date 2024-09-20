package de.sustineo.simdesk.services.auth;

import de.sustineo.simdesk.configuration.SecurityConfiguration;
import de.sustineo.simdesk.entities.auth.User;
import de.sustineo.simdesk.entities.auth.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Set;

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

        // Set custom attributes for the user
        LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("id", String.valueOf(user.getUserId()));
        attributes.put(SecurityConfiguration.ATTRIBUTE_AUTH_PROVIDER, SecurityConfiguration.AUTH_PROVIDER_DATABASE);

        // Set authorities for the user
        Set<? extends GrantedAuthority> authorities = userService.getAuthoritiesByUserId(user.getUserId());

        return new UserPrincipal(user, attributes, authorities);
    }
}
