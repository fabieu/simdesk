package de.sustineo.simdesk.socket;

import de.sustineo.simdesk.entities.PacketReceivedEvent;
import de.sustineo.simdesk.eventbus.EventBus;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.util.logging.Level;

@Log
public class AccSocketThread extends Thread {
    private static final int BUFFER_SIZE = 2048;
    private static final Duration SOCKET_TIMEOUT = Duration.ofSeconds(10);

    private final AccSocketState accSocketState;

    private final DatagramSocket socket;
    private boolean running = true;
    private boolean forceExit = false;

    public AccSocketThread(AccSocketState accSocketState) throws SocketException {
        super("ACC connection thread");
        this.accSocketState = accSocketState;

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());

        this.socket = new DatagramSocket();
        this.socket.setSoTimeout((int) SOCKET_TIMEOUT.toMillis());
        this.socket.connect(accSocketState.getHostAddress(), accSocketState.getHostPort());
    }

    private static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.log(Level.SEVERE, "Uncaught exception:", e);
        }
    }

    @Override
    public void run() {
        sendRegisterRequest();

        while (running) {
            try {
                DatagramPacket response = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                socket.receive(response);

                EventBus.publish(new PacketReceivedEvent(response.getData()));
            } catch (SocketTimeoutException e) {
                log.warning("ACC Socket timed out");
                running = false;
            } catch (PortUnreachableException e) {
                log.severe("ACC Socket is unreachable");
                running = false;
            } catch (SocketException e) {
                if (forceExit) {
                    log.info("ACC Socket was closed by user.");
                } else {
                    log.severe(String.format("ACC Socket closed unexpected: %s", e.getMessage()));
                }
                running = false;
            } catch (StackOverflowError | IOException e) {
                log.severe(String.format("Error in ACC listener thread: %s", e.getMessage()));
                running = false;
            }
        }
    }

    public void close() {
        super.interrupt();
        forceExit = true;
        socket.close();
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && super.isAlive() && running;
    }

    /**
     * Send register request.
     */
    public void sendRegisterRequest() {
        if (!isConnected()) {
            return;
        }

        sendRequest(AccSocketProtocol.buildRegisterRequest(
                accSocketState.getDisplayName(),
                accSocketState.getConnectionPassword(),
                accSocketState.getUpdateInterval(),
                accSocketState.getCommandPassword()
        ));
    }

    /**
     * Send unregister request.
     */
    public void sendUnregisterRequest() {
        if (!isConnected()) {
            return;
        }

        sendRequest(AccSocketProtocol.buildUnregisterRequest(accSocketState.getConnectionId()));
    }

    public void sendRequest(byte[] requestBytes) {
        if (socket.isConnected()) {
            try {
                socket.send(new DatagramPacket(requestBytes, requestBytes.length));
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error sending request.", e);
            }
        }
    }
}
