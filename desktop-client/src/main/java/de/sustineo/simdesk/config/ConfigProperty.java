package de.sustineo.simdesk.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConfigProperty {
    WEBSOCKET_URL("websocket.url", null),
    WEBSOCKET_API_KEY("websocket.apiKey", null),
    SESSION_ID("session.id", null),
    ;

    private final String key;
    private final String defaultValue;
}
