package de.sustineo.simdesk.socket;

import de.sustineo.simdesk.entities.PacketReceivedEvent;
import de.sustineo.simdesk.eventbus.Event;
import de.sustineo.simdesk.eventbus.EventBus;
import de.sustineo.simdesk.eventbus.EventListener;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public final class WebSocketClient implements EventListener {
    @Getter
    private static final WebSocketClient instance = new WebSocketClient();
    private StompClient stompClient;
    private String dashboardId;

    public synchronized void connect(@Nonnull String webSocketUrl, @Nullable String apiKey, @Nonnull String dashboardId) {
        if (stompClient != null && stompClient.isConnected()) {
            return;
        }

        this.dashboardId = dashboardId;
        this.stompClient = new StompClient(webSocketUrl, apiKey);

        stompClient.connect();
        EventBus.register(this);
    }

    public synchronized void disconnect() {
        if (stompClient == null) {
            return;
        }

        stompClient.disconnect();
        stompClient = null;

        EventBus.unregister(this);
    }

    @Override
    public void onEvent(Event event) {
        if (stompClient == null || dashboardId == null) {
            return;
        }

        if (event instanceof PacketReceivedEvent packetReceivedEvent) {
            stompClient.sendBytes("/app/livetiming", packetReceivedEvent.getPayload(), dashboardId);
        }
    }
}
