package de.sustineo.simdesk;

import javafx.application.Application;
import lombok.Getter;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SimDeskClientApplication {
    @Getter
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = new SpringApplicationBuilder(SimDeskClientApplication.class)
                .headless(false) // Enable JavaFX
                .web(WebApplicationType.NONE) // Disable web environment
                .run(args);
        Application.launch(JavaFxApplication.class, args);
    }
}
