package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.auth.UserPrincipal;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.discord.PermitService;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Profile;

import java.util.Optional;
import java.util.Set;

@Profile(ProfileManager.PROFILE_DISCORD)
@Route(value = "/profile", layout = MainLayout.class)
@PageTitle("Profile")
@PermitAll
public class UserProfileView extends BaseView {
    private final SecurityService securityService;
    private final Optional<PermitService> permitService;

    public UserProfileView(SecurityService securityService,
                           Optional<PermitService> permitService) {
        this.securityService = securityService;
        this.permitService = permitService;

        setSizeFull();
        setPadding(false);
        setAlignItems(Alignment.CENTER);

        Optional<UserPrincipal> user = securityService.getAuthenticatedUser();
        if (user.isEmpty()) {
            add(new Text("No user found"));
            return;
        }

        add(createUserHeader(), createPermitContainer());
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

    private Component createPermitContainer() {
        Div layout = new Div();
        layout.addClassNames("container", "bg-light");
        layout.add(createViewHeader("Driver-Permit"), createPermitDetails());

        return layout;
    }

    private Component createPermitDetails() {
        Optional<Long> userId = securityService.getAuthenticatedUser()
                .flatMap(UserPrincipal::getUserId);

        /* Permit details */
        Div permitDetailsLayout = new Div();
        permitDetailsLayout.addClassNames("pure-g");

        Div basePermitLayout = new Div();
        basePermitLayout.addClassNames("permit-details-container", "pure-u-1", "pure-u-sm-1-2");

        /* Base permit details > Permit Group */
        H4 basePermitStatusHeader = new H4("Base: ");
        Div basePermitStatusBadges = new Div();
        userId.flatMap(userIdValue -> permitService
                        .map(permitService -> permitService.getBasePermitBadge(userIdValue))
                )
                .ifPresent(basePermitStatusBadges::add);
        basePermitLayout.add(basePermitStatusHeader, basePermitStatusBadges);

        /* Base permit details > Car groups */
        VerticalLayout basePermittedCarGroupsLayout = new VerticalLayout();
        basePermittedCarGroupsLayout.setSpacing(false);
        basePermittedCarGroupsLayout.setPadding(false);
        Optional<Set<CarGroup>> basePermittedCarGroups = userId
                .flatMap(userIdValue -> permitService
                        .flatMap(permitService -> permitService.getBasePermittedCarGroups(userIdValue))
                );

        for (CarGroup carGroup : CarGroup.getValid()) {
            HorizontalLayout carGroupLayout = new HorizontalLayout();
            carGroupLayout.setPadding(false);

            if (basePermittedCarGroups.isPresent() && basePermittedCarGroups.get().contains(carGroup)) {
                FontIcon icon = new FontIcon("fa-solid", "fa-square-check");
                icon.setColor("var(--lumo-success-color)");
                carGroupLayout.add(icon);
            } else {
                FontIcon icon = new FontIcon("fa-solid", "fa-square-xmark");
                icon.setColor("var(--lumo-error-color)");
                carGroupLayout.add(icon);
            }
            carGroupLayout.add(new Text(carGroup.name()));
            basePermittedCarGroupsLayout.add(carGroupLayout);
        }
        basePermitLayout.add(basePermittedCarGroupsLayout);
        permitDetailsLayout.add(basePermitLayout);

        Div nosPermitLayout = new Div();
        nosPermitLayout.addClassNames("permit-details-container", "pure-u-1", "pure-u-sm-1-2");

        /* NOS permit details > Permit Groups */
        H4 nosPermitStatusHeader = new H4("NOS: ");
        Div nosPermitStatusBadges = new Div();
        userId.flatMap(userIdValue -> permitService
                        .map(permitService -> permitService.getNosPermitBadges(userIdValue))
                )
                .ifPresent(nosPermitStatusBadges::add);
        nosPermitLayout.add(nosPermitStatusHeader, nosPermitStatusBadges);

        /* NOS permit details > Car groups */
        VerticalLayout nosPermittedCarGroupsLayout = new VerticalLayout();
        nosPermittedCarGroupsLayout.setSpacing(false);
        nosPermittedCarGroupsLayout.setPadding(false);
        Optional<Set<CarGroup>> nosPermittedCarGroups = userId
                .flatMap(userIdValue -> permitService
                        .flatMap(permitService -> permitService.getNosPermittedCarGroups(userIdValue))
                );

        for (CarGroup carGroup : CarGroup.getValid()) {
            HorizontalLayout carGroupLayout = new HorizontalLayout();
            carGroupLayout.setPadding(false);

            if (nosPermittedCarGroups.isPresent() && nosPermittedCarGroups.get().contains(carGroup)) {
                FontIcon icon = new FontIcon("fa-solid", "fa-square-check");
                icon.setColor("var(--lumo-success-color)");
                carGroupLayout.add(icon);
            } else {
                FontIcon icon = new FontIcon("fa-solid", "fa-square-xmark");
                icon.setColor("var(--lumo-error-color)");
                carGroupLayout.add(icon);
            }
            carGroupLayout.add(new Text(carGroup.name()));

            nosPermittedCarGroupsLayout.add(carGroupLayout);
        }
        nosPermitLayout.add(nosPermittedCarGroupsLayout);

        permitDetailsLayout.add(nosPermitLayout);

        return permitDetailsLayout;
    }
}
