package de.sustineo.simdesk.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ApplicationContextProvider implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public <T> T getBean(Class<T> clazz) {
        return this.applicationContext.getBean(clazz);
    }

    public void exitApplication(int exitCode) {
        SpringApplication.exit(applicationContext, () -> exitCode);
        System.exit(exitCode);
    }
}
