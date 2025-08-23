package de.sustineo.simdesk.messaging;

import de.sustineo.simdesk.entities.auth.ApiKey;
import de.sustineo.simdesk.entities.auth.ApiKeyAuthenticationToken;
import de.sustineo.simdesk.services.auth.ApiKeyService;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationInterceptor implements ChannelInterceptor {
    private final ApiKeyService apiKeyService;

    @Override
    public Message<?> preSend(@Nonnull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String apiKeyFromHeader = accessor.getFirstNativeHeader(ApiKey.HEADER_NAME);

            Optional<ApiKey> apiKey = apiKeyService.getActiveByApiKey(apiKeyFromHeader);
            if (apiKey.isPresent()) {
                ApiKeyAuthenticationToken apiKeyToken = new ApiKeyAuthenticationToken(apiKey.get(), apiKey.get().getGrantedAuthorities());
                accessor.setUser(apiKeyToken);
            }
        }

        return message;
    }
}
