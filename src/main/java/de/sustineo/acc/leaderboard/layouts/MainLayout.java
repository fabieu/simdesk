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
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoIcon;
import de.sustineo.acc.leaderboard.configuration.Reference;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.utils.ApplicationContextProvider;
import de.sustineo.acc.leaderboard.views.*;
import org.springframework.boot.info.BuildProperties;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@PageTitle(VaadinConfiguration.APPLICATION_NAME)
public class MainLayout extends AppLayout {
    private static final String DEFAULT_THEME = Lumo.LIGHT;
    private static final String SESSION_ATTRIBUTE_THEME = "vaadin.custom.theme";
    private final BuildProperties buildProperties;
    private final Tabs leaderboardMenu;
    private final Tabs toolsMenu;
    private H1 viewTitle;

    public MainLayout(ApplicationContextProvider applicationContextProvider) {
        this.buildProperties = applicationContextProvider.getBean(BuildProperties.class);

        // Read and apply theme from session attribute if available
        String themeFromAttributes = (String) VaadinSession.getCurrent().getAttribute(SESSION_ATTRIBUTE_THEME);
        setTheme(Objects.requireNonNullElse(themeFromAttributes, DEFAULT_THEME));

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

        Span betaBadge = new Span("BETA");
        betaBadge.getElement().getThemeList().add("badge pill");
        betaBadge.getStyle().set("margin-left", "var(--lumo-space-s)");

        layout.add(viewTitle, betaBadge);

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
        String label = "Back to Home";

        Button homeButton = new Button(homeIcon);
        homeButton.setTooltipText(label);
        homeButton.setAriaLabel(label);
        homeButton.addClickListener(e -> UI.getCurrent().navigate(MainView.class));

        return homeButton;
    }

    private Button createThemeToggleButton() {
        String currentTheme = getTheme();

        Icon darkThemeIcon = VaadinIcon.MOON_O.create();
        Icon lightThemeIcon = VaadinIcon.SUN_O.create();
        String darkThemeLabel = "Enable dark theme";
        String lightThemeLabel = "Enable light theme";
        Icon themeButtonIcon = Lumo.DARK.equals(currentTheme) ? lightThemeIcon : darkThemeIcon;
        String label = Lumo.DARK.equals(currentTheme) ? lightThemeLabel : darkThemeLabel;

        Button themeButton = new Button(themeButtonIcon);
        themeButton.setTooltipText(label);
        themeButton.setAriaLabel(label);
        themeButton.addClickListener(e -> {
            if (Lumo.DARK.equals(getTheme())) {
                setTheme(Lumo.LIGHT);
                themeButton.setIcon(darkThemeIcon);
                themeButton.setTooltipText(darkThemeLabel);
                themeButton.setAriaLabel(darkThemeLabel);
            } else {
                setTheme(Lumo.DARK);
                themeButton.setIcon(lightThemeIcon);
                themeButton.setTooltipText(lightThemeLabel);
                themeButton.setAriaLabel(lightThemeLabel);
            }
        });

        return themeButton;
    }

    private void setTheme(String theme) {
        VaadinSession vaadinSession = UI.getCurrent().getSession();
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        themeList.removeAll(List.of(Lumo.DARK, Lumo.LIGHT));
        themeList.add(theme);
        vaadinSession.setAttribute(SESSION_ATTRIBUTE_THEME, theme);
    }

    private String getTheme() {
        return UI.getCurrent().getElement().getThemeList().stream()
                .findFirst()
                .orElse(DEFAULT_THEME);
    }

    private Component createHelpButton() {
        Icon helpIcon = VaadinIcon.QUESTION_CIRCLE_O.create();

        Button helpButton = new Button(helpIcon);
        helpButton.addClickListener(e -> UI.getCurrent().getPage().setLocation(Reference.PROJECT_WIKI));

        return helpButton;
    }

    private Component createDrawerContent(Tabs leaderboardMenu, Tabs toolsMenu) {
        Hr spacer = ComponentUtils.createSpacer();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);

        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.setPadding(false);
        menuLayout.setSpacing(false);
        menuLayout.add(leaderboardMenu);
        menuLayout.add(spacer);
        menuLayout.add(toolsMenu);

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        Span version = new Span("Version " + buildProperties.getVersion());
        version.getElement().getThemeList().add("badge");
        infoLayout.add(version);

        layout.addAndExpand(menuLayout);
        layout.add(infoLayout);

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
                createExternalTab("Entrylist Validator", VaadinIcon.COG.create(), Reference.SUSTINEO_ENTRYLIST_VALIDATOR),
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
