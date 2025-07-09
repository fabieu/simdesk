package de.sustineo.simdesk.views;

import de.sustineo.simdesk.client.AccBroadcastingClient;
import de.sustineo.simdesk.config.ConfigProperty;
import de.sustineo.simdesk.config.ConfigService;
import de.sustineo.simdesk.logging.TextAreaAppender;
import de.sustineo.simdesk.producer.WebSocketProducer;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log
@Component
public class MainView {
    private final ConfigService configService;
    private final BuildProperties buildProperties;

    private final AccBroadcastingClient accBroadcastingClient;
    private WebSocketProducer webSocketProducer;

    public MainView(ConfigService configService,
                    BuildProperties buildProperties) {
        this.configService = configService;
        this.buildProperties = buildProperties;
        this.accBroadcastingClient = AccBroadcastingClient.getClient();
    }

    public void start(Stage stage) {
        String initialWebsocketUrl = configService.getProperty(ConfigProperty.WEBSOCKET_URL);
        String initialWebsocketApiKey = configService.getProperty(ConfigProperty.WEBSOCKET_API_KEY);
        String initialSessionId = configService.getProperty(ConfigProperty.SESSION_ID);

        TextField urlField = new TextField();
        urlField.setText(initialWebsocketUrl);

        PasswordField apiKeyField = new PasswordField();
        apiKeyField.setText(initialWebsocketApiKey);

        TextField sessionField = new TextField(initialSessionId);
        sessionField.setText(initialSessionId);

        Button startButton = new Button("Start");
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        startButton.setPrefWidth(150);
        startButton.setMaxWidth(Double.MAX_VALUE);

        Button stopButton = new Button("Stop");
        stopButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
        stopButton.setPrefWidth(150);
        stopButton.setMaxWidth(Double.MAX_VALUE);

        startButton.setOnAction(e -> {
            String websocketUrl = urlField.getText();
            if (websocketUrl == null || websocketUrl.isEmpty()) {
                log.severe("Invalid configuration: WebSocket URL cannot be empty.");
                return;
            } else {
                websocketUrl = websocketUrl.trim();
            }

            String websocketApiKey = apiKeyField.getText();
            if (websocketApiKey == null || websocketApiKey.isEmpty()) {
                log.severe("Invalid configuration: WebSocket API Key cannot be empty.");
                return;
            } else {
                websocketApiKey = websocketApiKey.trim();
            }

            String sessionId = sessionField.getText();
            if (sessionId == null || sessionId.isEmpty()) {
                log.severe("Invalid configuration: Session ID cannot be empty.");
                return;
            } else {
                sessionId = sessionId.trim();
            }

            webSocketProducer = new WebSocketProducer(websocketUrl, websocketApiKey, sessionId);
            try {
                accBroadcastingClient.connectAutomatically();
                webSocketProducer.connect();

                if (accBroadcastingClient.isConnected()) {
                    startButton.setDisable(true);
                }
            } catch (SocketException ex) {
                log.severe("Failed to connect: " + ex.getMessage());
            }

            // Save the configuration properties
            Map<ConfigProperty, String> configProperties = new HashMap<>();
            configProperties.put(ConfigProperty.WEBSOCKET_URL, websocketUrl);
            configProperties.put(ConfigProperty.WEBSOCKET_API_KEY, websocketApiKey);
            configProperties.put(ConfigProperty.SESSION_ID, sessionId);
            configService.setProperties(configProperties);
        });

        stopButton.setOnAction(e -> {
            if (webSocketProducer != null) {
                webSocketProducer.disconnect();
            }

            accBroadcastingClient.disconnect();
            if (!accBroadcastingClient.isConnected()) {
                startButton.setDisable(false);
            }
        });

        // Buttons in right-side column
        VBox buttonBox = new VBox(10, startButton, stopButton);
        buttonBox.setFillWidth(true);
        buttonBox.setMaxHeight(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 0, 10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Column setup: Label | Input Field | Buttons
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHalignment(HPos.RIGHT);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.SOMETIMES); // allow right-side buttons to expand

        grid.getColumnConstraints().addAll(col1, col2, col3);

        grid.add(new Label("WebSocket URL:"), 0, 0);
        grid.add(urlField, 1, 0);
        grid.add(new Label("API Key:"), 0, 1);
        grid.add(apiKeyField, 1, 1);
        grid.add(new Label("Session ID:"), 0, 2);
        grid.add(sessionField, 1, 2);
        grid.add(buttonBox, 2, 0, 1, 3);

        TextArea logArea = new TextArea();
        logArea.setFont(Font.font("Monospaced", 13));
        logArea.setEditable(false);
        logArea.setWrapText(false);
        TextAreaAppender.addTextArea(logArea);

        Button clearLogAreaButton = new Button("Clear logs");
        clearLogAreaButton.setPrefWidth(100);
        clearLogAreaButton.setOnAction(e -> logArea.clear());

        HBox bottomBox = new HBox(clearLogAreaButton);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.setPadding(new Insets(0, 10, 10, 10));

        BorderPane root = new BorderPane();
        root.setTop(grid);
        root.setCenter(logArea);
        root.setBottom(bottomBox);
        BorderPane.setMargin(logArea, new Insets(10));

        Scene scene = new Scene(root, 700, 450);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/main.css")).toExternalForm());

        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icon.png"))));
        stage.setTitle(buildProperties.getName() + " " + buildProperties.getVersion());
        stage.setScene(scene);

        stage.show();
    }
}
