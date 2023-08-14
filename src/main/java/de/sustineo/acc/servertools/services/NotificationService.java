package de.sustineo.acc.servertools.services;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class NotificationService {
    private static final Duration DEFAULT_NOTIFICATION_DURATION = Duration.ofSeconds(5);

    public void showErrorNotification(String errorMessage) {
        showErrorNotification(errorMessage, DEFAULT_NOTIFICATION_DURATION);
    }

    public void showErrorNotification(String errorMessage, Duration duration) {
        Notification notification = new Notification();
        notification.setPosition(Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration((int) duration.toMillis());

        notification.add(new Text(errorMessage));
        notification.open();
    }
}
