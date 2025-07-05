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
    private static final int UPDATE_INTERVAL_DEFAULT = 100;

    private final ObjectMapper objectMapper;

    private static AccBroadcastingClient instance;
    private AccBroadcastingThread thread;
    private AccBroadcastingState accBroadcastingState = new AccBroadcastingState();

    private AccBroadcastingClient() {
        this.objectMapper = new ObjectMapper();

        EventBus.register(this);
    }

    public static AccBroadcastingClient getClient() {
        if (instance == null) {
            instance = new AccBroadcastingClient();
        }

        return instance;
    }

    public AccBroadcastingState getState() {
        return accBroadcastingState.copy();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ConnectionClosedEvent) {
            thread = null;
        }
    }

    /**
     * Connects to the game client.
     *
     * @param displayName        The display name of this connection.
     * @param connectionPassword The password for this connection.
     * @param commandPassword    The command password.
     * @param updateInterval     The interval in which to receive updates.
     * @param hostAddress        Host address of the server.
     * @param hostPort           Host port of the server.
     */
    public void connect(@NonNull String displayName, @NonNull String connectionPassword, @NonNull String commandPassword, int updateInterval, @NonNull InetAddress hostAddress, int hostPort) throws SocketException {
        if (thread != null) {
            return;
        }

        log.fine(String.format("Connecting to game with: displayName=%s, connectionPassword=%s, commandPassword=%s, updateInterval=%d, hostAddress=%s, hostPort=%d",
                displayName, connectionPassword, commandPassword, updateInterval, hostAddress, hostPort)
        );


        if (updateInterval < 0) {
            throw new IllegalArgumentException("Update interval cannot be less than 0");
        }

        accBroadcastingState = AccBroadcastingState.builder()
                .displayName(displayName)
                .connectionPassword(connectionPassword)
                .commandPassword(commandPassword)
                .updateInterval(updateInterval)
                .hostAddress(hostAddress)
                .hostPort(hostPort)
                .build();

        thread = new AccBroadcastingThread(accBroadcastingState);
        thread.start();
    }

    public void connectAutomatic() throws SocketException {
        log.fine("Try connecting with automatic settings");

        String filename = System.getProperty("user.home") + "/Documents/Assetto Corsa Competizione/Config/broadcasting.json";

        ConnectionInfo connectionInfo;
        try {
            connectionInfo = objectMapper.readValue(new File(filename), ConnectionInfo.class);
        } catch (IOException e) {
            throw new RuntimeException("Configuration file not found at " + filename);
        }

        connect("ACC Live timing",
                connectionInfo.getConnectionPassword(),
                connectionInfo.getCommandPassword(),
                UPDATE_INTERVAL_DEFAULT,
                InetAddress.getLoopbackAddress(),
                connectionInfo.getPort());
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
}
