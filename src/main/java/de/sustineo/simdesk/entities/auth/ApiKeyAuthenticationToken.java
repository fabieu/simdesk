package de.sustineo.simdesk.entities.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;

import java.util.Collection;

@Transient
public final class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
    private final ApiKey apiKey;

    public ApiKeyAuthenticationToken(ApiKey apiKey, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);

        this.apiKey = apiKey;
        this.setDetails(this.apiKey);
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return apiKey.getApiKey();
    }

    @Override
    public Object getPrincipal() {
        return apiKey.getUser().getUsername();
    }
}

