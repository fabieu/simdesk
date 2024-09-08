package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.entities.auth.UserPrincipal;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.auth.SecurityService;
import jakarta.annotation.security.PermitAll;

import java.util.Optional;

@Route(value = "/profile", layout = MainLayout.class)
@PageTitle("Profile")
@PermitAll
public class UserProfileView extends BaseView {
    private final SecurityService securityService;

    public UserProfileView(SecurityService securityService) {
        this.securityService = securityService;

        Optional<UserPrincipal> user = securityService.getAuthenticatedUser();
        if (user.isEmpty()) {
            add(new Text("No user found"));
            return;
        }

        setSizeFull();
        setPadding(false);
        setAlignItems(Alignment.CENTER);

        addAndExpand(createUserHeader());
        add(createFooter());
    }

    private Component createUserHeader() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);

        /* Avatar */
        Avatar avatar = new Avatar();
        avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
        layout.add(avatar);

        Optional<UserPrincipal> user = securityService.getAuthenticatedUser();
        user.ifPresent(userPrincipal -> avatar.setName(userPrincipal.getUsername()));

        Optional<String> avatarImageUrl = securityService.getAvatarUrl();
        avatarImageUrl.ifPresent(avatar::setImage);

        /* Name */
        layout.add(createNameLayout());

        return layout;
    }

    private Component createNameLayout() {
        VerticalLayout nameLayout = new VerticalLayout();
        nameLayout.setAlignItems(Alignment.CENTER);
        nameLayout.setPadding(false);
        nameLayout.setSpacing(false);

        Optional<UserPrincipal> user = securityService.getAuthenticatedUser();
        Optional<String> detailedName = user
                .map(UserPrincipal::getAttributes)
                .map(attributes -> attributes.get("global_name"))
                .map(String.class::cast);

        if (detailedName.isPresent()) {
            H3 detailedNameHeader = new H3(detailedName.get());
            Text usernameHeader = new Text(user.get().getUsername());
            nameLayout.add(detailedNameHeader, usernameHeader);
        } else if (user.isPresent()) {
            H3 usernameHeader = new H3(user.get().getUsername());
            nameLayout.add(usernameHeader);
        }

        return nameLayout;
    }
}
