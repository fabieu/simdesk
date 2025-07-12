package de.sustineo.simdesk.filter;

import de.sustineo.simdesk.services.auth.ApiKeyService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Log
@Component
@RequiredArgsConstructor
public class ApiKeyHandshakeInterceptor implements HandshakeInterceptor {
    private final ApiKeyService apiKeyService;

    @Override
    public boolean beforeHandshake(@Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response, @Nonnull WebSocketHandler wsHandler, @Nonnull Map<String, Object> attributes) {
        String apiKeyFromRequest = extractApiKeyFromRequest(request);
        if (apiKeyFromRequest != null) {
            return apiKeyService.getActiveByApiKey(apiKeyFromRequest).isPresent();
        }

        return false;
    }

    @Override
    public void afterHandshake(@Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response, @Nonnull WebSocketHandler wsHandler, Exception exception) {
        // No action needed after handshake
    }

    /**
     * Extracts the API key from the HTTPServletRequest by checking the following locations in order:
     * HTTP X-API-KEY header
     *
     * @param request The request to process
     * @return The api key string or null if none is found
     */
    protected String extractApiKeyFromRequest(ServerHttpRequest request) {
        // Retrieve api key from header "X-API-KEY"
        return request.getHeaders().getFirst("X-API-KEY");
    }
}
