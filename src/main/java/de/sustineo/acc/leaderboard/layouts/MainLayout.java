package de.sustineo.acc.leaderboard.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoIcon;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.views.*;

import java.util.Optional;


@PageTitle(VaadinConfiguration.APPLICATION_NAME)
public class MainLayout extends AppLayout implements RouterLayout {
    private final Tabs leaderboardMenu;
    private final Tabs toolsMenu;
    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.NAVBAR);
        addToNavbar(true, createNavbarContent(), createNavbarButtons());

        leaderboardMenu = createMenuTabs(createLeaderboardMenuTabs());
        toolsMenu = createMenuTabs(createToolsMenuTabs());
        addToDrawer(createDrawerContent(leaderboardMenu, toolsMenu));
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
        viewTitle = new H1(VaadinConfiguration.APPLICATION_NAME);
        viewTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        layout.add(viewTitle);

        return layout;
    }

    private Component createNavbarButtons() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.getStyle().set("margin", "0 var(--lumo-space-m)");

        layout.add(createHomeButton(), createThemeToggleButton());

        return layout;
    }

    private Component createHomeButton() {
        Icon homeIcon = VaadinIcon.HOME_O.create();

        Button homeButton = new Button(homeIcon);
        homeButton.addClickListener(e -> UI.getCurrent().navigate(MainView.class));

        return homeButton;
    }

    private Button createThemeToggleButton() {
        Icon darkThemeIcon = VaadinIcon.MOON_O.create();
        Icon lightThemeIcon = VaadinIcon.SUN_O.create();

        Button themeButton = new Button(darkThemeIcon);
        themeButton.addClickListener(e -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
                themeButton.setIcon(darkThemeIcon);
            } else {
                themeList.add(Lumo.DARK);
                themeButton.setIcon(lightThemeIcon);
            }
        });

        return themeButton;
    }

    private Component createHelpButton() {
        Icon helpIcon = VaadinIcon.QUESTION_CIRCLE_O.create();

        Button helpButton = new Button(helpIcon);
        helpButton.addClickListener(e -> UI.getCurrent().getPage().setLocation("https://gitlab.com/markracing/acc-leaderboard/-/wikis/home"));

        return helpButton;
    }

    private Component createDrawerContent(Tabs leaderboardMenu, Tabs toolsMenu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        Hr spacer = ComponentUtils.createSpacer();

        layout.add(leaderboardMenu);
        layout.add(spacer);
        layout.add(toolsMenu);

        return layout;
    }

    public static Tab[] createLeaderboardMenuTabs() {
        return new Tab[]{
                createTab("Home", VaadinIcon.HOME.create(), MainView.class),
                createTab("Lap Times", VaadinIcon.CLOCK.create(), OverallLapTimesView.class),
                createTab("Sessions", LumoIcon.ORDERED_LIST.create(), SessionView.class),
                createTab("Drivers", VaadinIcon.USERS.create(), DriverView.class),
        };
    }

    private Tab[] createToolsMenuTabs() {
        return new Tab[]{
                createExternalTab("Entrylist Validator", VaadinIcon.COG.create(), "https://acc.sustineo.de/entrylist"),
        };
    }

    private Tabs createMenuTabs(Tab[] tabEntries) {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(tabEntries);
        return tabs;
    }

    private static Tab createTab(String text, Icon icon, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(icon, new RouterLink(text, navigationTarget));
        tab.setId("tab-" + text.toLowerCase());
        ComponentUtil.setData(tab, Class.class, navigationTarget); // important for setting selected tab

        return tab;
    }

    private static Tab createExternalTab(String text, Icon icon, String navigationTarget) {
        Anchor link = new Anchor(navigationTarget, text);
        link.setTarget("_blank");

        Span badge = new Span();
        badge.setText("external");
        badge.getElement().getThemeList().add("badge pill error small");

        final Tab tab = new Tab();
        tab.add(icon, link, badge);
        tab.setId("tab-" + text.toLowerCase());
        tab.setSelected(false);

        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();

        if (getContent() != null) {
            // Select the tab corresponding to currently shown view
            getLeaderboardTabForComponent(getContent()).ifPresentOrElse(leaderboardMenu::setSelectedTab, () -> leaderboardMenu.setSelectedTab(null));
            getToolsTabForComponent(getContent()).ifPresentOrElse(toolsMenu::setSelectedTab, () -> toolsMenu.setSelectedTab(null));
            viewTitle.setText(getCurrentPageTitle());
        } else {
            leaderboardMenu.setSelectedTab(null);
            toolsMenu.setSelectedTab(null);
            viewTitle.setText(VaadinConfiguration.APPLICATION_NAME);
        }

        // Close drawer when navigating to different view
        setDrawerOpened(false);
    }

    private Optional<Tab> getLeaderboardTabForComponent(Component component) {
        return leaderboardMenu.getChildren()
                .filter(tab -> component.getClass().equals(ComponentUtil.getData(tab, Class.class)))
                .findFirst()
                .map(Tab.class::cast);
    }

    private Optional<Tab> getToolsTabForComponent(Component component) {
        return toolsMenu.getChildren()
                .filter(tab -> component.getClass().equals(ComponentUtil.getData(tab, Class.class)))
                .findFirst()
                .map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }
}
