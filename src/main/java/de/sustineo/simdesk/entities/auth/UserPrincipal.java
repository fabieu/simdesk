package de.sustineo.simdesk.entities.auth;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.*;

@Data
public class UserPrincipal implements UserDetails {
    private final String username;
    private final String password;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.attributes = new HashMap<>();
        this.authorities = new HashSet<>();
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

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Optional<Long> getUserId() {
        String key = "id";

        if (attributes == null || !attributes.containsKey(key)) {
            return Optional.empty();
        }

        return Optional.of(Long.parseLong((String) attributes.get(key)));
    }
}
