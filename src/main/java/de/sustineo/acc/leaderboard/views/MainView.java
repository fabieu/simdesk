package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.sustineo.acc.leaderboard.configuration.PropertyConfiguration;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.layouts.MainLayout;

@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "/dashboard", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME)
public class MainView extends VerticalLayout {

    public MainView(ComponentUtils componentUtils) {
        addClassName("dashboard-view");
        setSizeFull();

        addAndExpand(createMainContent());
        add(componentUtils.createFooter());
    }

    private Component createMainContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.addAndExpand(new H1("Welcome to ACC Leaderboard"));

        if ("development".equals(System.getenv(PropertyConfiguration.PROPERTY_ENVIRONMENT))){
            createUnderDevelopmentNotification();
        }


        return layout;
    }

    private void createUnderDevelopmentNotification() {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        notification.setPosition(Notification.Position.BOTTOM_END);
        notification.setDuration(10000); // 10 seconds

        Icon icon = VaadinIcon.WARNING.create();
        Div info = new Div(new Text(VaadinConfiguration.APPLICATION_NAME + " is currently under development and not yet finished."));

        Button closeButton = new Button(VaadinIcon.CLOSE.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(icon, info, closeButton);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }
}
