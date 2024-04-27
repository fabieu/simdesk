package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.configuration.VaadinConfiguration;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "/home", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Dashboard")
@AnonymousAllowed
public class MainView extends VerticalLayout {
    private final String communityName;


    public MainView(@Autowired(required = false) SessionService sessionService,
                    @Value("${simdesk.community.name}") String communityName) {
        this.communityName = communityName;

        setSizeFull();
        setPadding(false);

        add(createHeader());
        addAndExpand(createMainMenu());
    }

    private Component createHeader() {
        Div header = new Div();
        header.setId("home-header");
        String communityName = Optional.ofNullable(this.communityName)
                .map(name -> "SimDesk by " + name)
                .orElse("SimDesk");
        header.add(new H1(communityName));
        return header;
    }

    private Component createMainMenu() {
        Map<String, List<Tab>> categories = new LinkedHashMap<>();

        if (ProfileManager.isLeaderboardProfileEnabled()) {
            categories.put("Leaderboard", Arrays.stream(MainLayout.createLeaderboardMenuTabs()).toList());
        }

        if (ProfileManager.isBopProfileEnabled()) {
            categories.put("Balance of Performance", Arrays.stream(MainLayout.createBopMenuTabs()).toList());
        }

        if (ProfileManager.isEntrylistProfileEnabled()) {
            categories.put("Entrylist", Arrays.stream(MainLayout.createEntrylistMenuTabs()).toList());
        }

        Div menuContainer = new Div();
        menuContainer.addClassNames("home-menu-container", "pure-g");

        for (Map.Entry<String, List<Tab>> entry : categories.entrySet()) {
            Div menuCategory = new Div();
            menuCategory.addClassNames("home-menu-category", "pure-u-1", String.format("pure-u-md-1-%s", (int) Math.ceil((double) categories.size() / 2)), String.format("pure-u-lg-1-%s", categories.size()));

            menuCategory.add(new H3(entry.getKey()));

            for (Tab tab : entry.getValue()) {
                tab.addClassName("home-menu-tab");
                tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
                menuCategory.add(tab);
            }

            menuContainer.add(menuCategory);
        }

        return menuContainer;
    }
}
