package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import de.sustineo.acc.leaderboard.configuration.VaadinAppShellConfiguration;

import java.util.Optional;

/**
 * The main view contains a button and a click listener.
 */
@Route("")
public class MainView extends AppLayout {
    private final Tabs menu;
    private H1 viewTitle;
    private static final String DEFAULT_VIEW_TITLE = VaadinAppShellConfiguration.APPLICATION_NAME;

    public MainView() {
        setPrimarySection(Section.NAVBAR);
        addToNavbar(true, createNavbarContent());

        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
        setDrawerOpened(false);
    }

    private Component createNavbarContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Have the drawer toggle button on the left
        layout.add(new DrawerToggle());

        // Placeholder for the title of the current view.
        // The title will be set after navigation.
        viewTitle = new H1(DEFAULT_VIEW_TITLE);
        viewTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        layout.add(viewTitle);

        return layout;
    }

    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        // Add the menu to the drawer
        layout.add(menu);
        return layout;
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(createMenuTabs());
        return tabs;
    }

    private Tab[] createMenuTabs() {
        return new Tab[]{
                createTab("Home", MainView.class),
                createTab("Global Ranking", RankingGlobalView.class),
                createTab("Login", LoginView.class),
        };
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();

        if (getContent() != null) {
            // Select the tab corresponding to currently shown view
            getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
            viewTitle.setText(DEFAULT_VIEW_TITLE + " - "  + getCurrentPageTitle());
        } else {
            menu.setSelectedTab(null);
            viewTitle.setText(DEFAULT_VIEW_TITLE);
        }

        // Close drawer when navigating to different view
        setDrawerOpened(false);
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren()
                .filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst()
                .map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }
}
