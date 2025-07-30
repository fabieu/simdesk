package de.sustineo.simdesk.filter;

import de.sustineo.simdesk.entities.auth.ApiKey;
import de.sustineo.simdesk.entities.auth.ApiKeyAuthenticationToken;
import de.sustineo.simdesk.services.auth.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.Strings;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Optional;

@Order(1)
@Log
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final ApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        String apiKeyFromRequest = extractApiKeyFromRequest(request);
        if (apiKeyFromRequest != null) {
            Optional<ApiKey> apiKey = apiKeyService.getActiveByApiKey(apiKeyFromRequest);

            if (apiKey.isPresent()) {
                ApiKeyAuthenticationToken apiToken = new ApiKeyAuthenticationToken(apiKey.get(), apiKey.get().getGrantedAuthorities());
                SecurityContextHolder.getContext().setAuthentication(apiToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the API key from the HTTPServletRequest by checking the following locations in order:
     * HTTP authorization header, HTTP X-API-KEY header
     *
     * @param request The request to process
     * @return The api key string or null if none is found
     */
    protected String extractApiKeyFromRequest(HttpServletRequest request) {
        // Retrieve api key from header "Authorization", with Bearer prefix removed
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Strings.CI.startsWith(authorizationHeader, "Bearer ")) {
            return authorizationHeader.replaceAll("Bearer\\s*", "");
        }

        // Retrieve api key from header "X-API-KEY"
        return request.getHeader(ApiKey.HEADER_NAME);
    }
}
