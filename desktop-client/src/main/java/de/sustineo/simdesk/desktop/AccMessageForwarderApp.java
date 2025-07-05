package de.sustineo.simdesk.desktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.time.Duration;

/**
 * JavaFX application that listens for UDP messages from Assetto Corsa Competizione
 * and forwards them via WebSocket to a configurable endpoint without modifying the
 * received bytes.
 */
public class AccMessageForwarderApp extends Application {
    private TextField urlField;
    private TextField portField;
    private PasswordField passwordField;
    private TextArea logArea;
    private ListenerThread listenerThread;

    @Override
    public void start(Stage stage) {
        urlField = new TextField("ws://localhost:8080/acc");
        portField = new TextField("9000");
        passwordField = new PasswordField();
        passwordField.setPromptText("Optional");
        logArea = new TextArea();
        logArea.setEditable(false);

        Button startButton = new Button("Start");
        startButton.setOnAction(e -> startListening());
        Button stopButton = new Button("Stop");
        stopButton.setOnAction(e -> stopListening());

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.add(new Label("Forward URL:"), 0, 0);
        grid.add(urlField, 1, 0);
        grid.add(new Label("UDP Port:"), 0, 1);
        grid.add(portField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);

        HBox buttons = new HBox(5, startButton, stopButton);
        grid.add(buttons, 1, 3);

        BorderPane root = new BorderPane();
        root.setTop(grid);
        root.setCenter(logArea);

        stage.setTitle("ACC Message Forwarder");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    private synchronized void startListening() {
        if (listenerThread != null && listenerThread.isRunning()) {
            log("Already running");
            return;
        }
        try {
            int port = Integer.parseInt(portField.getText().trim());
            URI uri = URI.create(urlField.getText().trim());
            String password = passwordField.getText();
            listenerThread = new ListenerThread(port, uri, password);
            listenerThread.start();
            log("Started listening on UDP port " + port + " and forwarding to " + uri);
        } catch (NumberFormatException ex) {
            log("Invalid port: " + portField.getText());
        }
    }

    private synchronized void stopListening() {
        if (listenerThread != null) {
            listenerThread.shutdown();
            listenerThread = null;
            log("Stopped listening");
        }
    }

    private void log(String msg) {
        Platform.runLater(() -> logArea.appendText(msg + "\n"));
    }

    private class ListenerThread extends Thread {
        private final int port;
        private final URI uri;
        private final String password;
        private volatile boolean running = true;
        private DatagramSocket socket;
        private final HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        private WebSocket webSocket;

        ListenerThread(int port, URI uri, String password) {
            this.port = port;
            this.uri = uri;
            this.password = password;
            setName("AccListenerThread");
        }

        boolean isRunning() {
            return running;
        }

        void shutdown() {
            running = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (webSocket != null) {
                try {
                    webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "bye").join();
                } catch (Exception ignored) {
                    // ignore
                }
            }
        }

        @Override
        public void run() {
            try {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(port));

                webSocket = httpClient.newWebSocketBuilder()
                        .connectTimeout(Duration.ofSeconds(5))
                        .buildAsync(uri, new WebSocket.Listener() {
                            @Override
                            public void onError(WebSocket ws, Throwable error) {
                                log("WebSocket error: " + error.getMessage());
                            }
                        }).join();
                log("Connected to " + uri);

                if (password != null && !password.isEmpty()) {
                    byte[] pwdBytes = password.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    DatagramPacket p = new DatagramPacket(pwdBytes, pwdBytes.length,
                            InetAddress.getByName("127.0.0.1"), port);
                    socket.send(p);
                    log("Sent UDP password handshake");
                }

                byte[] buffer = new byte[65535];
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    byte[] data = new byte[packet.getLength()];
                    System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
                    forward(data);
                }
            } catch (IOException e) {
                if (running) {
                    log("Socket error: " + e.getMessage());
                }
            } finally {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
        }

        private void forward(byte[] data) {
            if (webSocket == null) {
                log("WebSocket not connected");
                return;
            }
            webSocket.sendBinary(ByteBuffer.wrap(data), true)
                    .whenComplete((ws, throwable) -> {
                        if (throwable != null) {
                            log("Failed to forward packet: " + throwable.getMessage());
                        } else {
                            log("Forwarded packet");
                        }
                    });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
