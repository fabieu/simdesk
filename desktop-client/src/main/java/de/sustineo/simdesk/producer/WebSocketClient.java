package de.sustineo.simdesk.producer;

import de.sustineo.simdesk.client.events.PacketReceivedEvent;
import de.sustineo.simdesk.eventbus.Event;
import de.sustineo.simdesk.eventbus.EventBus;
import de.sustineo.simdesk.eventbus.EventListener;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.java.Log;

import java.util.Objects;

@Log
public class WebSocketClient implements EventListener {
    private final String dashboardId;
    private final StompClient stompClient;

    public WebSocketClient(@Nonnull String webSocketUrl, @Nullable String apiKey, @Nonnull String dashboardId) {
        this.dashboardId = Objects.requireNonNull(dashboardId);
        this.stompClient = new StompClient(webSocketUrl, apiKey);
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
        if (event instanceof PacketReceivedEvent packetReceivedEvent) {
            stompClient.sendBytes("/app/livetiming", packetReceivedEvent.getPayload(), dashboardId);
        }
    }
}
