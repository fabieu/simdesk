package de.sustineo.simdesk.entities.auth;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Data
public class UserPrincipal implements UserDetails {
    private final String username;
    private final String password;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user, Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.attributes = attributes;
        this.authorities = authorities;
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
}
