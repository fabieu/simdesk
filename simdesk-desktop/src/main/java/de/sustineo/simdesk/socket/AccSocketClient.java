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
     * Attempts to establish a connection to the ACC (Assetto Corsa Competizione) socket server
     * using the default configuration file located at:
     * <pre>
     *   ${user.home}/Documents/Assetto Corsa Competizione/Config/broadcasting.json
     * </pre>
     * <p>
     * If the configuration file cannot be found or read, an {@link IllegalArgumentException} is thrown.
     *
     * @throws SocketException          if an error occurs while attempting to establish the socket connection
     * @throws IllegalArgumentException if the configuration file does not exist or cannot be parsed
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
     * Establishes a new connection to an ACC (Assetto Corsa Competizione) socket server.
     * <p>
     * This method initializes a new {@link AccSocketState} with the provided connection details,
     * logs the connection attempt, and starts an {@link AccSocketThread} to handle communication.
     * <p>
     * The method is synchronized to ensure only one thread can attempt a connection at a time.
     * If a connection thread is already running, the method returns immediately without creating a new connection.
     *
     * @param connectionPassword the password required for establishing the connection (must not be {@code null})
     * @param commandPassword    the password required for issuing commands (must not be {@code null})
     * @param hostAddress        the target host's {@link InetAddress} (must not be {@code null})
     * @param hostPort           the target host port
     * @throws SocketException if there is a socket-related error while starting the connection
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
     * Closes the active connection to the ACC (Assetto Corsa Competizione) socket server, if any.
     * <p>
     * This method is synchronized to prevent concurrent disconnect attempts. If no connection
     * thread is running, the method returns immediately.
     */
    public synchronized void disconnect() {
        if (thread == null) {
            return;
        }

        thread.close();
        thread = null;
    }

    /**
     * Sends a request to the ACC (Assetto Corsa Competizione) socket server over the active connection.
     * <p>
     * This method is synchronized to ensure thread-safe access to the underlying socket thread.
     * If no connection is currently active, the request is ignored.
     * Otherwise, the given byte array is forwarded to {@link AccSocketThread#sendRequest(byte[])}
     * for transmission.
     *
     * @param requestBytes the request payload to send
     */
    public synchronized void sendRequest(byte[] requestBytes) {
        if (thread == null || requestBytes == null) {
            return;
        }

        thread.sendRequest(requestBytes);
    }

    /**
     * Sends a register request to the ACC (Assetto Corsa Competizione) socket server
     * over the active connection.
     * <p>
     * This method is synchronized to ensure thread-safe access to the underlying socket thread.
     * If no connection is currently active, the request is ignored.
     */
    public synchronized void sendRegisterRequest() {
        if (thread == null) {
            return;
        }

        thread.sendRegisterRequest();
    }
}
