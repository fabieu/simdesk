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
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.configuration.VaadinConfiguration;
import de.sustineo.simdesk.layouts.MainLayout;

@Route(value = "login", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_SHORT_PREFIX + "Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        login.setAction("login");

        add(createOAuth2LoginLayout(), login);
    }

    private Component createOAuth2LoginLayout() {
        HorizontalLayout layout = new HorizontalLayout();

        if (ProfileManager.isDiscordProfileEnabled()) {
            FontIcon discordIcon = new FontIcon("fa-brands", "fa-discord");
            HorizontalLayout discordLinkLayout = new HorizontalLayout(discordIcon, new Text("Login with Discord"));
            discordLinkLayout.setAlignItems(Alignment.CENTER);

            Anchor discordLoginLink = new Anchor("login/oauth2/authorization/discord", discordLinkLayout);
            discordLoginLink.getStyle()
                    .setColor("white")
                    .setTextDecoration("none");
            discordLoginLink.getElement().setAttribute("router-ignore", "");

            Button discordButton = new Button(discordLoginLink);
            discordButton.getStyle()
                    .setBackgroundColor("#5865f2")
                    .setPadding("1.5em 2em");

            layout.add(discordButton);
        }

        return layout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}