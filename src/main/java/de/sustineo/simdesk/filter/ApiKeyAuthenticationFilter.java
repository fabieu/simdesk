package de.sustineo.simdesk.filter;

import de.sustineo.simdesk.entities.auth.ApiKey;
import de.sustineo.simdesk.entities.auth.ApiKeyAuthenticationToken;
import de.sustineo.simdesk.services.auth.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Order(1)
@Component
@Log
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final ApiKeyService apiKeyService;

    @Autowired
    public ApiKeyAuthenticationFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        String apiKeyFromRequest = extractApiKeyFromRequest(request);
        if (apiKeyFromRequest != null) {
            Optional<ApiKey> apiKey = apiKeyService.getActiveByApiKey(apiKeyFromRequest);

            if (apiKey.isPresent()) {
                List<GrantedAuthority> grantedAuthorities = AuthorityUtils.NO_AUTHORITIES;
                if (apiKey.get().getRoles() != null) {
                    List<String> roles = apiKey.get().getRoles().stream()
                            .map(Enum::name)
                            .toList();
                    grantedAuthorities = AuthorityUtils.createAuthorityList(roles);
                }

                ApiKeyAuthenticationToken apiToken = new ApiKeyAuthenticationToken(apiKey.get(), grantedAuthorities);
                SecurityContextHolder.getContext().setAuthentication(apiToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the API key from the HTTPServletRequest by checking the following locations in order:
     * HTTP authorization header, Request parameter ('apiKey')
     *
     * @param request The request to process
     * @return The api key string or null if none is found
     */
    protected String extractApiKeyFromRequest(HttpServletRequest request) {
        // Retrieve api key from header "Authorization", with Bearer prefix removed
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.startsWith(authorizationHeader, "Bearer ")) {
            return authorizationHeader.replaceAll("Bearer\\s*", "");
        }

        // Retrieve api key from header "x-api-key"
        return request.getHeader("x-api-key");
    }
}
