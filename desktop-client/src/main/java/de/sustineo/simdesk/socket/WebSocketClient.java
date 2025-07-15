package de.sustineo.simdesk.socket;

import de.sustineo.simdesk.entities.PacketReceivedEvent;
import de.sustineo.simdesk.eventbus.Event;
import de.sustineo.simdesk.eventbus.EventBus;
import de.sustineo.simdesk.eventbus.EventListener;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
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

/**
 * WebSocketClient is responsible for managing the connection to a STOMP server over WebSocket.
 * It handles connection, disconnection, and message sending, as well as automatic reconnection
 * in case of connection loss.
 */
@Log
@Component
public class WebSocketClient implements EventListener {
    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final Duration RECONNECT_DELAY = Duration.ofSeconds(3);

    private static final String SOCKET_DEST_LIVE_TIMING = "/app/acc/live-timing";
    private static final String SOCKET_QUEUE_REQUEST = "/user/queue/acc/request";

    private final AccSocketClient accSocketClient;
    private final WebSocketStompClient webSocketStompClient;
    private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private final ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();

    private StompSession stompSession;
    private final Object sessionLock = new Object();
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);

    private volatile String webSocketUrl;
    private volatile String apiKey;
    private volatile String dashboardId;

    public WebSocketClient(AccSocketClient accSocketClient) {
        this.accSocketClient = accSocketClient;

        this.taskScheduler.initialize();
        this.webSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.webSocketStompClient.setTaskScheduler(taskScheduler);
    }

    @PreDestroy
    public void shutdown() {
        reconnectScheduler.shutdownNow();
        taskScheduler.shutdown();
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
     * Connects to the STOMP server using the provided WebSocket URL, API key, and dashboard ID.
     *
     * @param webSocketUrl The WebSocket URL to connect to.
     * @param apiKey       The API key for authentication (can be null).
     * @param dashboardId  The dashboard ID to use for the connection.
     */
    public void connect(@Nonnull String webSocketUrl, @Nullable String apiKey, @Nonnull String dashboardId) {
        if (isConnected()) {
            return;
        }

        this.webSocketUrl = webSocketUrl;
        this.apiKey = apiKey;
        this.dashboardId = dashboardId;

        doConnect();

        log.info(String.format("Connecting to WebSocket [%s] with dashboardId [%s]", webSocketUrl, dashboardId));
    }

    /**
     * Disconnects from the STOMP server and unregisters the EventBus listener.
     */
    public void disconnect() {
        EventBus.unregister(this);

        synchronized (sessionLock) {
            if (stompSession != null) {
                stompSession.disconnect();
                stompSession = null;

                log.info(String.format("Closed WebSocket to [%s] due to user request", webSocketUrl));
            }
        }

        reconnecting.set(false);
    }

    private void doConnect() {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        if (apiKey != null) {
            headers.add(API_KEY_HEADER, apiKey);
        }

        webSocketStompClient.connectAsync(webSocketUrl, headers, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(@Nonnull StompSession session, @Nonnull StompHeaders connectedHeaders) {
                synchronized (sessionLock) {
                    if (stompSession != null && stompSession.isConnected()) {
                        stompSession.disconnect();
                    }
                    stompSession = session;
                }

                reconnecting.set(false);

                subscribeToSocketRequestCallback(session);
                EventBus.register(WebSocketClient.this);
            }

            @Override
            public void handleTransportError(@Nonnull StompSession session, @Nonnull Throwable exception) {
                EventBus.unregister(WebSocketClient.this);

                log.severe(String.format("Transport error: %s", exception.getMessage()));
                scheduleReconnect();
            }
        });
    }

    /**
     * Schedules a reconnection attempt after a delay if the connection is lost.
     */
    private void scheduleReconnect() {
        if (!reconnecting.compareAndSet(false, true)) {
            return;
        }

        reconnectScheduler.schedule(() -> {
            log.info(String.format("Attempting reconnection to WebSocket [%s]", webSocketUrl));
            if (!isConnected()) {
                doConnect();
            }
        }, RECONNECT_DELAY.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Sends a byte array payload to the specified destination with the given dashboard ID.
     *
     * @param destination The STOMP destination to send the payload to.
     * @param payload     The byte array payload to send.
     * @param dashboardId The dashboard ID to include in the message headers.
     */
    private void sendBytes(String destination, byte[] payload, String dashboardId) {
        synchronized (sessionLock) {
            if (stompSession == null || !stompSession.isConnected()) {
                log.warning("Dropping payload, no active session");
                return;
            }

            StompHeaders headers = new StompHeaders();
            headers.setDestination(destination);
            headers.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM);
            headers.set("dashboard-id", dashboardId);

            stompSession.send(headers, payload);
        }
    }

    private void subscribeToSocketRequestCallback(StompSession session) {
        session.subscribe(SOCKET_QUEUE_REQUEST, new StompSessionHandlerAdapter() {
            @Override
            @Nonnull
            public Type getPayloadType(@Nonnull StompHeaders headers) {
                return byte[].class;
            }

            @Override
            public void handleFrame(@Nonnull StompHeaders headers, Object payload) {
                try {
                    accSocketClient.sendRequest((byte[]) payload);
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error handling socket request", ex);
                }
            }

            @Override
            public void handleException(@Nonnull StompSession session, @Nullable StompCommand command, @Nonnull StompHeaders headers, @Nonnull byte[] payload, @Nonnull Throwable exception) {
                log.log(Level.SEVERE, "Error handling socket request", exception);
            }
        });
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof PacketReceivedEvent packetReceivedEvent) {
            sendBytes(SOCKET_DEST_LIVE_TIMING, packetReceivedEvent.getPayload(), dashboardId);
        }
    }
}
