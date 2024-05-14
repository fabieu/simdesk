package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.configuration.VaadinConfiguration;
import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.auth.UserPrincipal;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.services.discord.PermitService;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Profile(ProfileManager.PROFILE_DISCORD)
@Route(value = "/permit/me", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Permit")
@PermitAll
public class PermitUserView extends VerticalLayout {
    private final SecurityService securityService;
    private final Optional<PermitService> permitService;

    public PermitUserView(SecurityService securityService,
                          Optional<PermitService> permitService) {
        this.securityService = securityService;
        this.permitService = permitService;

        setSizeFull();
        setPadding(false);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);

        Optional<UserPrincipal> user = securityService.getAuthenticatedUser();
        if (user.isEmpty()) {
            layout.add(new Text("No user found"));
            add(layout);
            return;
        }

        layout.add(createUserHeader(), createPermitContainer());

        add(layout);
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
        layout.addClassNames("container", "bg-light", "pure-g");
        layout.add(createPermitHeading(), createPermitDetails(), createPermitExplanation());

        return layout;
    }

    private Component createPermitHeading() {
        H2 heading = new H2("Driver-Permit");
        heading.addClassNames("pure-u-1");
        return heading;
    }

    private VerticalLayout createPermitDetails() {
        /* Permit details */
        VerticalLayout permitDetailsLayout = new VerticalLayout();
        permitDetailsLayout.setWidth(null);
        permitDetailsLayout.addClassNames("pure-u-1", "pure-u-md-1-5");

        Div basePermitLayout = new Div();
        basePermitLayout.addClassNames("permit-details-container");

        /* Base permit details > Permit Group */
        H4 basePermitStatusHeader = new H4("Base: ");
        Div basePermitStatusBadges = new Div();
        permitService.map(PermitService::getBasePermitBadge).ifPresent(basePermitStatusBadges::add);
        basePermitLayout.add(basePermitStatusHeader, basePermitStatusBadges);

        /* Base permit details > Car groups */
        VerticalLayout basePermittedCarGroupsLayout = new VerticalLayout();
        basePermittedCarGroupsLayout.setSpacing(false);
        basePermittedCarGroupsLayout.setPadding(false);
        Optional<List<CarGroup>> basePermittedCarGroups = permitService.flatMap(PermitService::getBasePermittedCarGroups);
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
        nosPermitLayout.addClassNames("permit-details-container");

        /* NOS permit details > Permit Groups */
        H4 nosPermitStatusHeader = new H4("NOS: ");
        Div nosPermitStatusBadges = new Div();
        permitService.map(PermitService::getNosPermitBadges).ifPresent(nosPermitStatusBadges::add);

        nosPermitLayout.add(nosPermitStatusHeader, nosPermitStatusBadges);

        /* NOS permit details > Car groups */
        VerticalLayout nosPermittedCarGroupsLayout = new VerticalLayout();
        nosPermittedCarGroupsLayout.setSpacing(false);
        nosPermittedCarGroupsLayout.setPadding(false);
        Optional<List<CarGroup>> permittedCarGroups = permitService.flatMap(PermitService::getNosPermittedCarGroups);
        for (CarGroup carGroup : CarGroup.getValid()) {
            HorizontalLayout carGroupLayout = new HorizontalLayout();
            carGroupLayout.setPadding(false);

            if (permittedCarGroups.isPresent() && permittedCarGroups.get().contains(carGroup)) {
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

    private Component createPermitExplanation() {
        /* Explanation */
        VerticalLayout explanationLayout = new VerticalLayout();
        explanationLayout.addClassNames("pure-u-1", "pure-u-md-4-5");
        explanationLayout.setWidth(null);
        explanationLayout.setSpacing(false);

        Set<String> permitGroups = permitService.map(PermitService::getAllBasePermitGroups).orElse(Set.of());
        boolean hasPermit = permitService.map(PermitService::hasBasePermitGroup).orElse(false);

        Paragraph introduction = new Paragraph(String.format("Participating drivers must have a permit %s for certain events. Drivers are assigned one of %s license levels based on their previous experience and performance. A driver may be reclassified by his behavior on- and off-track.", permitGroups, permitGroups.size()));
        String newPermitExplanationHtml = """
                <div class='permit-explanation'>
                    <p><strong>STEP 1:</strong> To fulfill this step, you must complete a stint with the vehicle class corresponding to your desired license level, comprising the following components:</p>
                    <ul>
                        <li>Outlap</li>
                        <li>1 Base lap</li>
                        <li>3 Confirmation laps (Ensure the delta to the base lap remains within ±0.500s)</li>
                        <li>2 Hotlaps (Strive to achieve your optimal time)</li>
                        <li>Inlap</li>
                    </ul>
                    <p>Ensure that the laps are completed in the specified sequence and are valid. Submissions must include screenshots as proof: <a href='https://discord.com/channels/705817819459092500/1148637094672089200' target='_blank'>SUBMIT</a></p>
                    <p><strong>STEP 2:</strong> In addition, participation in a sighting race is required. Any sprint event advertised by Sim2Real or a special sighting event can be used as a sighting race.</p>
                </div>
                """;
        Details newPermitDetails = new Details("New Permit", new Html(newPermitExplanationHtml));

        String upgradePermitExplanationHtml = """
                <div class='permit-explanation'>
                    <p>Drivers who have already acquired a permit may upgrade their license level by participating in a sighting race with the vehicle class corresponding to their desired license level. </p>
                </div>
                """;
        Details upgradePermitDetails = new Details("Upgrade Permit", new Html(upgradePermitExplanationHtml));

        String nosPermitExplanationHtml = """
                <div class='permit-explanation'>
                    <p>A supplementary "Permit-NOS" is mandatory for races held on the Nordschleife circuit. To acquire this permit extension, you must possess the foundational driver permit for your chosen vehicle class (or a higher tier), as well as the corresponding Permit-NOS-X.</p>
                    <p>To obtain the Permit-NOS, you must fulfill the following requirements using a vehicle matching your desired license level:</p>
                    <ul>
                        <li>Outlap</li>
                        <li>2 Hotlaps (with lap times below the maximum lap time)</li>
                        <li>Inlap</li>
                    </ul>
                    <table>
                        <tr>
                            <th>Vehicle Class</th>
                            <th>Permit-NOS</th>
                            <th> Maximum Lap Time</th>
                        </tr>
                        <tr>
                            <td>GT3</td>
                            <td>Permit-NOS-SP9</td>
                            <td>8:30.000</td>
                        </tr>
                        <tr>
                            <td>GT2</td>
                            <td>Permit-NOS-SPX</td>
                            <td>8:35.000</td>
                        </tr>
                        <tr>
                            <td>GTC</td>
                            <td>Permit-NOS-CUP2</td>
                            <td>8:45.000</td>
                        </tr>
                        <tr>
                            <td>GT4</td>
                            <td>Permit-NOS-SP10</td>
                            <td>9:20.000</td>
                        </tr>
                        <tr>
                            <td>TCX</td>
                            <td>Permit-NOS-CUP5</td>
                            <td>10:00.000</td>
                        </tr>
                    </table>
                    <p>Ensure that the laps are completed in the specified sequence and are valid. Submissions must include screenshots as proof: <a href='https://discord.com/channels/705817819459092500/1148637094672089200' target='_blank'>SUBMIT</a></p>
                </div>
                """;
        Details nosPermitDetails = new Details("Nordschleife Permit", new Html(nosPermitExplanationHtml));

        if (!hasPermit) {
            newPermitDetails.setOpened(true);
        } else {
            upgradePermitDetails.setOpened(true);
            nosPermitDetails.setOpened(true);
        }

        explanationLayout.add(introduction, newPermitDetails, upgradePermitDetails, nosPermitDetails);
        return explanationLayout;
    }
}
