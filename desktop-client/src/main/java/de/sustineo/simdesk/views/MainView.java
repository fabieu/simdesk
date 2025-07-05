package de.sustineo.simdesk.views;

import de.sustineo.simdesk.client.AccBroadcastingClient;
import de.sustineo.simdesk.eventbus.Event;
import de.sustineo.simdesk.eventbus.EventBus;
import de.sustineo.simdesk.eventbus.EventListener;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.SocketException;

@Component
public class MainView implements EventListener {
    private final AccBroadcastingClient client;

    private TextArea logArea;

    public MainView() {
        this.client = AccBroadcastingClient.getClient();

        EventBus.register(this);
    }

    public void start(Stage stage) {
        TextField urlField = new TextField("ws://localhost:8080/acc");
        logArea = new TextArea();
        logArea.setEditable(false);

        Button startButton = new Button("Start");
        startButton.setOnAction(e -> {
            try {
                client.connectAutomatic();
            } catch (SocketException ex) {
                log("Failed to connect: " + ex.getMessage());
            }
        });
        Button stopButton = new Button("Stop");
        stopButton.setOnAction(e -> client.disconnect());

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.add(new Label("Forward URL:"), 0, 0);
        grid.add(urlField, 1, 0);

        HBox buttons = new HBox(5, startButton, stopButton);
        grid.add(buttons, 1, 3);

        BorderPane root = new BorderPane();
        root.setTop(grid);
        root.setCenter(logArea);

        stage.setTitle("ACC Message Forwarder");
        stage.setScene(new Scene(root, 600, 400));
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
