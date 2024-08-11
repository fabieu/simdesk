package de.sustineo.simdesk.entities.auth;

import de.sustineo.simdesk.configuration.SecurityConfiguration;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Data
public class UserPrincipal implements UserDetails {
    private final String username;
    private final String password;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.attributes = user.getAttributes();
        this.authorities = user.getAuthorities();
    }

    public UserPrincipal(DefaultOAuth2User user) {
        this.username = user.getName();
        this.password = null;
        this.attributes = user.getAttributes();
        this.authorities = user.getAuthorities();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public boolean isDiscordUser() {
        return determineAuthProvider(SecurityConfiguration.AUTH_PROVIDER_DISCORD);
    }

    public boolean isDatabaseUser() {
        return determineAuthProvider(SecurityConfiguration.AUTH_PROVIDER_DATABASE);
    }

    private boolean determineAuthProvider(String authProvider) {
        return attributes != null && authProvider.equals(attributes.get(SecurityConfiguration.ATTRIBUTE_AUTH_PROVIDER));
    }

    public Optional<Long> getUserId() {
        String attributeName = "id";

        if (attributes == null || !attributes.containsKey(attributeName)) {
            return Optional.empty();
        }

        return Optional.of(Long.parseLong((String) attributes.get(attributeName)));
    }
}
