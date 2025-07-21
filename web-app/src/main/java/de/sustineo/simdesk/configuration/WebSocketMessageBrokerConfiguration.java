package de.sustineo.simdesk.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.web.csrf.XorCsrfChannelInterceptor;
import org.springframework.security.messaging.web.socket.server.CsrfTokenHandshakeInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketMessageBrokerConfiguration implements WebSocketMessageBrokerConfigurer {
    private final AuthorizationManager<Message<?>> messageAuthorizationManager;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .addInterceptors(new CsrfTokenHandshakeInterceptor())
                .setAllowedOriginPatterns("*"); // TODO: Set allowed origins properly in production
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
                .setUserDestinationPrefix("/user")
                .setApplicationDestinationPrefixes("/app") // @MessageMapping
                .enableSimpleBroker("/topic", "/queue");

    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                new XorCsrfChannelInterceptor(),
                new AuthorizationChannelInterceptor(messageAuthorizationManager)
        );
    }
}
