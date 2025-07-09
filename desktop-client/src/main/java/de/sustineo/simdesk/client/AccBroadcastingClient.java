package de.sustineo.simdesk.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sustineo.simdesk.client.events.ConnectionClosedEvent;
import de.sustineo.simdesk.client.protocol.ConnectionInfo;
import de.sustineo.simdesk.eventbus.Event;
import de.sustineo.simdesk.eventbus.EventBus;
import de.sustineo.simdesk.eventbus.EventListener;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

@Log
public class AccBroadcastingClient implements EventListener {
    private static AccBroadcastingClient instance;
    private static AccBroadcastingThread thread;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AccBroadcastingClient() {
        EventBus.register(this);
    }

    public static AccBroadcastingClient getClient() {
        if (instance == null) {
            instance = new AccBroadcastingClient();
        }

        return instance;
    }

    /**
     * Connects to the game client with automatic settings.
     * The settings are read from the broadcasting.json file in the user's Documents folder.
     *
     * @throws SocketException if there is an error creating the socket.
     */
    public void connectAutomatically() throws SocketException {
        String filename = System.getProperty("user.home") + "/Documents/Assetto Corsa Competizione/Config/broadcasting.json";

        log.fine("Try connecting with automatic settings from " + filename);

        ConnectionInfo connectionInfo;
        try {
            connectionInfo = objectMapper.readValue(new File(filename), ConnectionInfo.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Configuration file not found at " + filename);
        }

        connect(connectionInfo.getConnectionPassword(),
                connectionInfo.getCommandPassword(),
                InetAddress.getLoopbackAddress(),
                connectionInfo.getPort());
    }

    /**
     * Connects to the game client.
     *
     * @param connectionPassword The password for this connection.
     * @param commandPassword    The command password.
     * @param hostAddress        Host address of the server.
     * @param hostPort           Host port of the server.
     */
    public void connect(@NonNull String connectionPassword, @NonNull String commandPassword, @NonNull InetAddress hostAddress, int hostPort) throws SocketException {
        if (thread != null) {
            return;
        }

        AccBroadcastingState accBroadcastingState = AccBroadcastingState.builder()
                .connectionPassword(connectionPassword)
                .commandPassword(commandPassword)
                .hostAddress(hostAddress)
                .hostPort(hostPort)
                .build();

        log.fine(String.format("Connecting to ACC with: displayName=%s, connectionPassword=%s, commandPassword=%s, updateInterval=%d, hostAddress=%s, hostPort=%d",
                accBroadcastingState.getDisplayName(),
                accBroadcastingState.getConnectionPassword(),
                accBroadcastingState.getCommandPassword(),
                accBroadcastingState.getUpdateInterval(),
                accBroadcastingState.getHostAddress(),
                accBroadcastingState.getHostPort()
        ));

        thread = new AccBroadcastingThread(accBroadcastingState);
        thread.start();
    }

    /**
     * Disconnects from the game client.
     */
    public void disconnect() {
        if (thread == null) {
            return;
        }

        thread.sendUnregisterRequest();
        thread.close();
    }

    /**
     * Checks if the client is connected to the game.
     *
     * @return true if connected, false otherwise.
     */
    public boolean isConnected() {
        if (thread != null) {
            return thread.isConnected();
        }

        return false;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ConnectionClosedEvent) {
            thread = null;
        }
    }
}
