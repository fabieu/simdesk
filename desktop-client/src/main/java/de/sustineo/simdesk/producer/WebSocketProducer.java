package de.sustineo.simdesk.producer;

import de.sustineo.simdesk.eventbus.Event;
import de.sustineo.simdesk.eventbus.EventBus;
import de.sustineo.simdesk.eventbus.EventListener;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.java.Log;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Log
public class WebSocketProducer implements EventListener {
    private final StompBroadcastClient stompClient;
    private final String sessionId;

    public WebSocketProducer(@Nonnull String webSocketUrl, @Nullable String apiKey, @Nonnull String sessionId) {
        this.sessionId = Objects.requireNonNull(sessionId);

        this.stompClient = new StompBroadcastClient(webSocketUrl, apiKey);
    }

    public void connect() {
        EventBus.register(this);
        stompClient.connect();
    }

    public void disconnect() {
        EventBus.unregister(this);
        stompClient.disconnect();
    }

    @Override
    public void onEvent(Event event) {
        log.info("Publish to WebSocket: " + event.toString());
        stompClient.sendBytes(sessionId, event.toString().getBytes(StandardCharsets.UTF_8));
    }
}
