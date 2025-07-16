package de.sustineo.simdesk.views;

import de.sustineo.simdesk.config.ConfigProperty;
import de.sustineo.simdesk.config.ConfigService;
import de.sustineo.simdesk.logging.TextAreaAppender;
import de.sustineo.simdesk.socket.AccSocketClient;
import de.sustineo.simdesk.socket.WebSocketClient;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log
@Component
@RequiredArgsConstructor
public class MainView {
    private final AccSocketClient accSocketClient;
    private final WebSocketClient webSocketClient;
    private final ConfigService configService;
    private final BuildProperties buildProperties;

    public void start(Stage stage) {
        String initialWebsocketUrl = configService.getProperty(ConfigProperty.WEBSOCKET_URL);
        String initialWebsocketApiKey = configService.getProperty(ConfigProperty.WEBSOCKET_API_KEY);
        String initialDashboardId = configService.getProperty(ConfigProperty.DASHBOARD_ID);

        Label websocketUrlLabel = new Label("WebSocket URL:");
        TextField websocketUrlField = new TextField();
        if (initialWebsocketUrl == null) {
            websocketUrlField.setPromptText("wss://example.com/ws"); // Example placeholder
        } else {
            websocketUrlField.setText(initialWebsocketUrl);
        }

        Label websocketApiKeyLabel = new Label("API Key:");
        PasswordField websocketApiKeyField = new PasswordField();
        if (initialWebsocketApiKey == null) {
            websocketApiKeyField.setPromptText("********************************"); // Example placeholder
        } else {
            websocketApiKeyField.setText(initialWebsocketApiKey);
        }

        Label dashboardIdLabel = new Label("Dashboard ID:");
        TextField dashboardIdField = new TextField(initialDashboardId);
        if (initialDashboardId == null) {
            dashboardIdField.setPromptText("l0cuoMojUk"); // Example placeholder
        } else {
            dashboardIdField.setText(initialDashboardId);
        }

        Button startButton = new Button("Start");
        startButton.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white; -fx-font-weight: bold;");
        startButton.setPrefWidth(150);
        startButton.setMaxWidth(Double.MAX_VALUE);

        Button stopButton = new Button("Stop");
        stopButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold;");
        stopButton.setPrefWidth(150);
        stopButton.setMaxWidth(Double.MAX_VALUE);

        startButton.setOnAction(e -> {
            String websocketUrl = websocketUrlField.getText();
            if (websocketUrl == null || websocketUrl.isEmpty()) {
                log.severe("Invalid configuration: WebSocket URL is missing.");
                return;
            } else {
                websocketUrl = websocketUrl.trim();
            }

            String websocketApiKey = websocketApiKeyField.getText();
            if (websocketApiKey == null || websocketApiKey.isEmpty()) {
                log.severe("Invalid configuration: WebSocket API Key is missing.");
                return;
            } else {
                websocketApiKey = websocketApiKey.trim();
            }

            String dashboardId = dashboardIdField.getText();
            if (dashboardId == null || dashboardId.isEmpty()) {
                log.severe("Invalid configuration: Dashboard ID is missing.");
                return;
            } else {
                dashboardId = dashboardId.trim();
            }

            try {
                accSocketClient.connectAutomatically();
                webSocketClient.connect(websocketUrl, websocketApiKey, dashboardId);
            } catch (SocketException ex) {
                log.severe("Failed to connect: " + ex.getMessage());
            }

            // Save the configuration properties
            Map<ConfigProperty, String> configProperties = new HashMap<>();
            configProperties.put(ConfigProperty.WEBSOCKET_URL, websocketUrl);
            configProperties.put(ConfigProperty.WEBSOCKET_API_KEY, websocketApiKey);
            configProperties.put(ConfigProperty.DASHBOARD_ID, dashboardId);
            configService.setProperties(configProperties);
        });

        stopButton.setOnAction(e -> {
            accSocketClient.disconnect();
            webSocketClient.disconnect();
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

        grid.add(websocketUrlLabel, 0, 0);
        grid.add(websocketUrlField, 1, 0);
        grid.add(websocketApiKeyLabel, 0, 1);
        grid.add(websocketApiKeyField, 1, 1);
        grid.add(dashboardIdLabel, 0, 2);
        grid.add(dashboardIdField, 1, 2);
        grid.add(buttonBox, 2, 0, 1, 3);

        TextArea logArea = new TextArea();
        logArea.setFont(Font.font("Monospaced", 13));
        logArea.setEditable(false);
        logArea.setWrapText(false);
        logArea.setFocusTraversable(false);
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

        // Do not focus any input field on startup
        Platform.runLater(root::requestFocus);
    }
}
