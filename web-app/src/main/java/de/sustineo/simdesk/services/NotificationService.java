package de.sustineo.simdesk.services;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.sustineo.simdesk.entities.NotificationType;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class NotificationService {
    private static final Duration DEFAULT_NOTIFICATION_DURATION = Duration.ofSeconds(5);

    public void showSuccessNotification(String successMessage) {
        showSuccessNotification(DEFAULT_NOTIFICATION_DURATION, successMessage);
    }

    public void showSuccessNotification(Duration duration, String successMessage) {
        showSuccessNotification(duration, new Text(successMessage));
    }

    public void showSuccessNotification(Component... components) {
        showSuccessNotification(DEFAULT_NOTIFICATION_DURATION, components);
    }

    public void showSuccessNotification(Duration duration, Component... components) {
        showNotification(NotificationType.SUCCESS, duration, components);
    }

    public void showWarningNotification(String warningMessage) {
        showWarningNotification(DEFAULT_NOTIFICATION_DURATION, warningMessage);
    }

    public void showWarningNotification(Duration duration, String warningMessage) {
        showWarningNotification(duration, new Text(warningMessage));
    }

    public void showWarningNotification(Component... components) {
        showWarningNotification(DEFAULT_NOTIFICATION_DURATION, components);
    }

    public void showWarningNotification(Duration duration, Component... components) {
        showNotification(NotificationType.WARNING, duration, components);
    }

    public void showErrorNotification(String errorMessage) {
        showErrorNotification(DEFAULT_NOTIFICATION_DURATION, errorMessage);
    }

    public void showErrorNotification(Duration duration, String errorMessage) {
        showErrorNotification(duration, new Text(errorMessage));
    }

    public void showErrorNotification(Component... components) {
        showErrorNotification(DEFAULT_NOTIFICATION_DURATION, components);
    }

    public void showErrorNotification(Duration duration, Component... components) {
        showNotification(NotificationType.ERROR, duration, components);
    }

    public void showInfoNotification(String infoMessage) {
        showInfoNotification(DEFAULT_NOTIFICATION_DURATION, infoMessage);
    }

    public void showInfoNotification(Duration duration, String infoMessage) {
        showInfoNotification(duration, new Text(infoMessage));
    }

    public void showInfoNotification(Component... components) {
        showInfoNotification(DEFAULT_NOTIFICATION_DURATION, components);
    }

    public void showInfoNotification(Duration duration, Component... components) {
        showNotification(NotificationType.INFO, duration, components);
    }

    private void showNotification(NotificationType notificationType, Duration duration, Component... components) {
        Notification notification = new Notification();
        notification.setPosition(Notification.Position.TOP_END);
        notification.addThemeVariants(getNotificationVariant(notificationType));
        notification.setDuration((int) duration.toMillis());

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon icon = getNotificationIcon(notificationType);
        icon.setSize("var(--lumo-icon-size-s)");

        Button closeButton = new Button(VaadinIcon.CLOSE_SMALL.create(), clickEvent -> notification.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        horizontalLayout.add(icon, new Div(components), closeButton);

        notification.add(horizontalLayout);
        notification.open();
    }

    private NotificationVariant getNotificationVariant(NotificationType notificationType) {
        return switch (notificationType) {
            case SUCCESS -> NotificationVariant.LUMO_SUCCESS;
            case WARNING -> NotificationVariant.LUMO_WARNING;
            case ERROR -> NotificationVariant.LUMO_ERROR;
            case INFO -> NotificationVariant.LUMO_PRIMARY;
        };
    }

    private Icon getNotificationIcon(NotificationType notificationType) {
        return switch (notificationType) {
            case SUCCESS -> VaadinIcon.CHECK_CIRCLE.create();
            case WARNING -> VaadinIcon.EXCLAMATION_CIRCLE.create();
            case ERROR -> VaadinIcon.CLOSE_CIRCLE.create();
            default -> VaadinIcon.INFO_CIRCLE.create();
        };
    }
}
