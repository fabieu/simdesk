package de.sustineo.simdesk;

import de.sustineo.simdesk.views.MainView;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {
    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        this.context = SimDeskClientApplication.getContext();
    }

    @Override
    public void start(Stage primaryStage) {
        MainView mainView = context.getBean(MainView.class);
        mainView.start(primaryStage);
    }

    @Override
    public void stop() throws Exception {
        context.close();
        super.stop();
    }
}
