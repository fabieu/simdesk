package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.services.NotificationService;

@Route(value = "login")
@AnonymousAllowed
public class LoginView extends BaseView {
    private final NotificationService notificationService;
    private final LoginForm login = new LoginForm();
    private Button loginButton = new Button();

    public LoginView(NotificationService notificationService) {
        this.notificationService = notificationService;

        addClassName("login-view");
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        login.setAction("login");
        login.setVisible(false);

        add(createLoginButtonLayout(), login);
    }

    @Override
    public String getPageTitle() {
        return "Login";
    }

    private Component createLoginButtonLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMaxWidth("calc(var(--lumo-size-m) * 10)");

        if (ProfileManager.isDiscordProfileEnabled()) {
            layout.add(createDiscordLoginButton());
        }

        layout.add(createStandardLoginButton());

        return layout;
    }

    private Button createStandardLoginButton() {
        FontIcon loginIcon = new FontIcon("fa-solid", "fa-user");
        HorizontalLayout buttonLayout = new HorizontalLayout(loginIcon, new Text("Login via User"));
        buttonLayout.setAlignItems(Alignment.CENTER);

        loginButton = new Button(buttonLayout);
        loginButton.addClassNames("login-button", "user");
        loginButton.addClickListener(event -> {
            login.setVisible(true);
            loginButton.setVisible(false);
        });

        return loginButton;
    }

    private Button createDiscordLoginButton() {
        FontIcon discordIcon = new FontIcon("fa-brands", "fa-discord");
        HorizontalLayout discordLinkLayout = new HorizontalLayout(discordIcon, new Text("Login via Discord"));
        discordLinkLayout.setAlignItems(Alignment.CENTER);

        Anchor discordLoginLink = new Anchor("login/oauth2/authorization/discord", discordLinkLayout);
        discordLoginLink.getElement().setAttribute("router-ignore", "");

        Button discordButton = new Button(discordLoginLink);
        discordButton.addClassNames("login-button", "discord");

        return discordButton;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginButton.setVisible(false);
            login.setVisible(true);
            login.setError(true);
        } else if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("oauth-error")) {
            notificationService.showErrorNotification("Login failed! Please try again later.");
        }
    }
}