package de.sustineo.simdesk.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConfigProperty {
    WEBSOCKET_HOST("websocket.host", null),
    WEBSOCKET_API_KEY("websocket.apiKey", null),
    DASHBOARD_ID("dashboard.id", null),
    ;

    private final String key;
    private final String defaultValue;
}
