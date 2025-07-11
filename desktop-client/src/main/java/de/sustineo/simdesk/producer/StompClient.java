package de.sustineo.simdesk.producer;

import de.sustineo.simdesk.client.AccBroadcastingClient;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.java.Log;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log
class StompClient {
    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final Duration RECONNECT_DELAY = Duration.ofSeconds(3);

    private final String webSocketUrl;
    private final String apiKey;

    private final WebSocketStompClient webSocketStompClient;
    private StompSession stompSession;

    private final Object sessionLock = new Object();

    private final AccBroadcastingClient accBroadcastingClient = AccBroadcastingClient.getClient();

    private final ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();

    public StompClient(@Nonnull String webSocketUrl, @Nullable String apiKey) {
        this.webSocketUrl = webSocketUrl;
        this.apiKey = apiKey;

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();

        this.webSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.webSocketStompClient.setTaskScheduler(scheduler);
    }

    public void connect() {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

        if (apiKey != null) {
            headers.add(API_KEY_HEADER, apiKey);
        }

        webSocketStompClient.connectAsync(webSocketUrl, headers, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(@Nonnull StompSession session, @Nonnull StompHeaders connectedHeaders) {
                log.info(String.format("Connected to WebSocket connection [%s]", webSocketUrl));
                synchronized (sessionLock) {
                    stompSession = session;

                    session.subscribe("/session/queue/acc/requests", new StompSessionHandlerAdapter() {
                        @Override
                        @Nonnull
                        public Type getPayloadType(@Nonnull StompHeaders headers) {
                            return byte[].class;
                        }

                        @Override
                        public void handleFrame(@Nonnull StompHeaders headers, Object payload) {
                            if (payload instanceof byte[] requestBytes) {
                                accBroadcastingClient.sendRequest(requestBytes);
                            }
                        }
                    });
                }
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                log.severe(String.format("Transport error: %s", exception.getMessage()));
                attemptReconnect();
            }
        });
    }

    private void attemptReconnect() {
        reconnectScheduler.schedule(() -> {
            log.info(String.format("Attempting reconnect to WebSocket connection [%s]", webSocketUrl));
            connect();
        }, RECONNECT_DELAY.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void disconnect() {
        reconnectScheduler.shutdownNow();
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
        log.info(String.format("Closed WebSocket connection to [%s]", webSocketUrl));
    }

    public void sendBytes(String destination, byte[] payload) {
        if (stompSession != null && stompSession.isConnected()) {
            StompHeaders headers = new StompHeaders();
            headers.setDestination(destination);
            headers.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM);

            stompSession.send(headers, payload);
        }
    }
}
