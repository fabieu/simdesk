package de.sustineo.simdesk.views;

import de.sustineo.simdesk.client.AccBroadcastingClient;
import de.sustineo.simdesk.eventbus.Event;
import de.sustineo.simdesk.eventbus.EventBus;
import de.sustineo.simdesk.eventbus.EventListener;
import de.sustineo.simdesk.producer.WebSocketProducer;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.net.SocketException;
import java.util.Objects;

@Component
public class MainView implements EventListener {
    private final BuildProperties buildProperties;
    private final AccBroadcastingClient accBroadcastingClient;
    private WebSocketProducer webSocketProducer;

    private TextArea logArea;


    public MainView(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
        this.accBroadcastingClient = AccBroadcastingClient.getClient();

        EventBus.register(this);
    }

    public void start(Stage stage) {
        TextField urlField = new TextField();
        TextField sessionField = new TextField();
        PasswordField apiKeyField = new PasswordField();

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);

        Button startButton = new Button("Start");
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        startButton.setPrefWidth(150);
        startButton.setMaxWidth(Double.MAX_VALUE);
        startButton.setDisable(false);

        Button stopButton = new Button("Stop");
        stopButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
        stopButton.setPrefWidth(150);
        stopButton.setMaxWidth(Double.MAX_VALUE);
        stopButton.setDisable(true);

        startButton.setOnAction(e -> {
            String websocketUrl = urlField.getText().trim();
            String sessionId = sessionField.getText().trim();
            String apiKey = apiKeyField.getText().trim();
            webSocketProducer = new WebSocketProducer(websocketUrl, apiKey, sessionId);
            try {
                accBroadcastingClient.connectAutomatic();
                webSocketProducer.connect();

                if (accBroadcastingClient.isConnected()) {
                    startButton.setDisable(true);
                    stopButton.setDisable(false);
                }
            } catch (SocketException ex) {
                log("Failed to connect: " + ex.getMessage());
            }
        });

        stopButton.setOnAction(e -> {
            accBroadcastingClient.disconnect();
            webSocketProducer.disconnect();

            if (!accBroadcastingClient.isConnected()) {
                startButton.setDisable(false);
                stopButton.setDisable(true);
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

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
        grid.add(new Label("Session ID:"), 0, 1);
        grid.add(sessionField, 1, 1);
        grid.add(new Label("API Key:"), 0, 2);
        grid.add(apiKeyField, 1, 2);

        // Buttons in right-side column
        VBox buttonBox = new VBox(10, startButton, stopButton);
        buttonBox.setFillWidth(true);
        buttonBox.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(buttonBox, Priority.ALWAYS);

        VBox.setVgrow(startButton, Priority.ALWAYS);
        VBox.setVgrow(stopButton, Priority.ALWAYS);

        grid.add(buttonBox, 2, 0, 1, 3);

        BorderPane root = new BorderPane();
        root.setTop(grid);
        root.setCenter(logArea);
        BorderPane.setMargin(logArea, new Insets(10));

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/main.css")).toExternalForm());

        stage.setTitle("SimDesk Desktop Client - " + buildProperties.getVersion());
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icon.png"))));

        stage.show();
    }

    @Override
    public void onEvent(Event event) {
        log(event.toString());
    }

    private void log(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }
}
