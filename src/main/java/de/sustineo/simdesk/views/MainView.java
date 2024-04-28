package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.VaadinConfiguration;
import de.sustineo.simdesk.entities.menu.MenuItem;
import de.sustineo.simdesk.entities.menu.MenuItemCategory;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.MenuService;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "/home", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Dashboard")
@AnonymousAllowed
public class MainView extends VerticalLayout {
    private final MenuService menuService;
    private final String communityName;


    public MainView(MenuService menuService,
                    @Value("${simdesk.community.name}") String communityName) {
        this.menuService = menuService;
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
        Map<MenuItemCategory, List<MenuItem>> menuMap = menuService.getItemsByCategory();
        menuMap.remove(MenuItemCategory.MAIN);
        menuMap.remove(MenuItemCategory.EXTERNAL_LINKS);

        Div menuContainer = new Div();
        menuContainer.addClassNames("home-menu-container", "pure-g");

        for (Map.Entry<MenuItemCategory, List<MenuItem>> entry : menuMap.entrySet()) {
            Div menuCategory = new Div();
            menuCategory.addClassNames("home-menu-category", "pure-u-1", String.format("pure-u-md-1-%s", (int) Math.ceil((double) menuMap.size() / 2)), String.format("pure-u-lg-1-%s", menuMap.size()));

            menuCategory.add(new H3(entry.getKey().getName()));

            for (MenuItem menuItem : entry.getValue()) {
                Button button = new Button(menuItem.getName(), menuItem.getIcon());
                button.addClassName("home-menu-button");
                button.addClickListener(event -> {
                    switch (menuItem.getType()) {
                        case INTERNAL:
                            getUI().ifPresent(ui -> ui.navigate(menuItem.getNavigationTarget()));
                            break;
                        case EXTERNAL:
                            //TODO: Implement external link handling
                            break;
                    }
                });

                menuCategory.add(button);
            }

            menuContainer.add(menuCategory);
        }

        return menuContainer;
    }
}
