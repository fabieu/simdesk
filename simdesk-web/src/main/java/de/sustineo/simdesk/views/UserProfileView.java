package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.entities.auth.UserPrincipal;
import de.sustineo.simdesk.services.auth.SecurityService;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.GrantedAuthority;

import java.util.Optional;

@Route(value = "/profile")
@PermitAll
public class UserProfileView extends BaseView {
    private final SecurityService securityService;
    private final Optional<UserPrincipal> user;

    public UserProfileView(SecurityService securityService) {
        this.securityService = securityService;
        this.user = securityService.getAuthenticatedUser();

        if (user.isEmpty()) {
            add(new Text("No user found"));
            return;
        }

        setSizeFull();
        setPadding(false);
        setAlignItems(Alignment.CENTER);

        addAndExpand(createUserHeader());
    }

    @Override
    public String getPageTitle() {
        return "Profile";
    }

    private Component createUserHeader() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);

        /* Avatar */
        Avatar avatar = new Avatar();
        avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
        layout.add(avatar);

        user.ifPresent(userPrincipal -> avatar.setName(userPrincipal.getUsername()));

        Optional<String> avatarImageUrl = securityService.getAvatarUrl();
        avatarImageUrl.ifPresent(avatar::setImage);

        /* Name */
        user.ifPresent(userPrincipal -> layout.add(createNameLayout(userPrincipal)));

        /* Roles */
        user.ifPresent(userPrincipal -> layout.add(createRolesLayout(userPrincipal)));

        return layout;
    }

    private Component createNameLayout(UserPrincipal userPrincipal) {
        VerticalLayout nameLayout = new VerticalLayout();
        nameLayout.setAlignItems(Alignment.CENTER);
        nameLayout.setPadding(false);
        nameLayout.setSpacing(false);

        Optional<String> globalName = Optional.of(userPrincipal)
                .flatMap(UserPrincipal::getGlobalName);

        if (globalName.isPresent()) {
            H3 globalNameHeader = new H3(globalName.get());
            Text usernameHeader = new Text(userPrincipal.getUsername());
            nameLayout.add(globalNameHeader, usernameHeader);
        } else {
            H3 usernameHeader = new H3(userPrincipal.getUsername());
            nameLayout.add(usernameHeader);
        }

        return nameLayout;
    }

    private Component createRolesLayout(UserPrincipal userPrincipal) {
        HorizontalLayout rolesLayout = new HorizontalLayout();

        for (GrantedAuthority role : userPrincipal.getAuthorities()) {
            Span roleSpan = new Span(role.getAuthority());
            roleSpan.getElement().getThemeList().add("badge");
            rolesLayout.add(roleSpan);
        }

        return rolesLayout;
    }
}
