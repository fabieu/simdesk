package de.sustineo.simdesk.socket;

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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@Log
class StompClient {
    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final Duration RECONNECT_DELAY = Duration.ofSeconds(3);

    private final String webSocketUrl;
    private final String apiKey;

    private final WebSocketStompClient webSocketStompClient;
    private volatile StompSession stompSession;
    private final ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();

    private final Object sessionLock = new Object();
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);

    private final AccSocketClient accSocketClient = AccSocketClient.getInstance();

    public StompClient(@Nonnull String webSocketUrl, @Nullable String apiKey) {
        this.webSocketUrl = webSocketUrl;
        this.apiKey = apiKey;

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();

        this.webSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.webSocketStompClient.setTaskScheduler(scheduler);
    }

    /**
     * Checks if the STOMP session is currently connected to the WebSocket server.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        synchronized (sessionLock) {
            return stompSession != null && stompSession.isConnected();
        }
    }

    /**
     * Connects to the WebSocket server using STOMP protocol.
     * If already connected, this method does nothing.
     * If an API key is provided, it will be included in the handshake headers.
     */
    public void connect() {
        if (isConnected()) {
            return;
        }

        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
        if (apiKey != null) {
            handshakeHeaders.add(API_KEY_HEADER, apiKey);
        }

        webSocketStompClient.connectAsync(webSocketUrl, handshakeHeaders, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(@Nonnull StompSession session, @Nonnull StompHeaders connectedHeaders) {
                log.info(String.format("Connected to websocket [%s]", webSocketUrl));
                synchronized (sessionLock) {
                    stompSession = session;

                    subscribeToAccQueue(session);
                }
            }

            @Override
            public void handleTransportError(@Nonnull StompSession session, @Nonnull Throwable exception) {
                log.severe(String.format("Transport error: %s", exception.getMessage()));
                attemptReconnect();
            }
        });
    }

    /**
     * Attempts to reconnect to the WebSocket server after a delay.
     * This method uses an AtomicBoolean to ensure that only one reconnection attempt is made at a time.
     */
    private void attemptReconnect() {
        if (!reconnecting.compareAndSet(false, true)) {
            return;
        }

        reconnectScheduler.schedule(() -> {
            log.info(String.format("Attempting reconnect to websocket [%s]", webSocketUrl));
            reconnecting.set(false);
            if (!isConnected()) {
                connect();
            }
        }, RECONNECT_DELAY.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Closes the STOMP client and disconnects from the WebSocket server.
     * This method stops any ongoing reconnection attempts and safely disconnects the session.
     */
    public void disconnect() {
        // 1. Stop trying to reconnect
        reconnecting.set(false);
        reconnectScheduler.shutdownNow();

        // 2. Disconnect session safely
        synchronized (sessionLock) {
            if (stompSession != null) {
                try {
                    stompSession.disconnect();
                } catch (Exception e) {
                    log.log(Level.WARNING, "Error while disconnecting STOMP session", e);
                } finally {
                    stompSession = null;
                }
            }
        }

        log.info(String.format("Closed websocket to [%s]", webSocketUrl));
    }

    /**
     * Sends a byte array payload to the specified destination with the given dashboard ID.
     * This method is synchronized to ensure thread safety when accessing the STOMP session.
     *
     * @param destination The STOMP destination to send the message to.
     * @param payload     The byte array payload to send.
     * @param dashboardId The ID of the dashboard associated with this message.
     */
    public void sendBytes(String destination, byte[] payload, String dashboardId) {
        synchronized (sessionLock) {
            if (stompSession != null && stompSession.isConnected()) {
                StompHeaders headers = new StompHeaders();
                headers.setDestination(destination);
                headers.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM);
                headers.set("dashboardId", dashboardId);

                stompSession.send(headers, payload);
            }
        }
    }

    /**
     * Subscribes to the ACC request queue on the STOMP server.
     * This method listens for incoming requests and forwards them to the AccSocketClient.
     *
     * @param session The STOMP session to subscribe to.
     */
    private void subscribeToAccQueue(StompSession session) {
        String destination = "/user/queue/acc.request";

        session.subscribe(destination, new StompSessionHandlerAdapter() {
            @Override
            @Nonnull
            public Type getPayloadType(@Nonnull StompHeaders headers) {
                return byte[].class;
            }

            @Override
            public void handleFrame(@Nonnull StompHeaders headers, Object payload) {
                accSocketClient.sendRequest((byte[]) payload);
            }
        });

        log.info(String.format("Subscribed to destination [%s]", destination));
    }
}
