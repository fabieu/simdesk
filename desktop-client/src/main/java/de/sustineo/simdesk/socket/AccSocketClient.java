package de.sustineo.simdesk.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sustineo.simdesk.entities.ConnectionInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

@Log
@Component
@RequiredArgsConstructor
public class AccSocketClient {
    private final ObjectMapper objectMapper;

    private AccSocketThread thread;

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
    public synchronized void connect(@NonNull String connectionPassword, @NonNull String commandPassword, @NonNull InetAddress hostAddress, int hostPort) throws SocketException {
        if (thread != null) {
            return;
        }

        AccSocketState accSocketState = AccSocketState.builder()
                .connectionPassword(connectionPassword)
                .commandPassword(commandPassword)
                .hostAddress(hostAddress)
                .hostPort(hostPort)
                .build();

        log.info(String.format("Connecting to AccSocket with displayName=%s, connectionPassword=%s, commandPassword=%s, updateInterval=%d, hostAddress=%s, hostPort=%d",
                accSocketState.getDisplayName(),
                accSocketState.getConnectionPassword(),
                accSocketState.getCommandPassword(),
                accSocketState.getUpdateInterval(),
                accSocketState.getHostAddress(),
                accSocketState.getHostPort()
        ));

        thread = new AccSocketThread(accSocketState);
        thread.start();
    }

    /**
     * Disconnects from the game client.
     */
    public synchronized void disconnect() {
        if (thread == null) {
            return;
        }

        thread.sendUnregisterRequest();
        thread.close();
        thread = null;
    }

    /**
     * Checks if the client is connected to the game.
     *
     * @return true if connected, false otherwise.
     */
    public synchronized boolean isConnected() {
        if (thread != null) {
            return thread.isConnected();
        }

        return false;
    }

    public synchronized void sendRequest(byte[] requestBytes) {
        if (!isConnected()) {
            return;
        }

        thread.sendRequest(requestBytes);
    }
}
