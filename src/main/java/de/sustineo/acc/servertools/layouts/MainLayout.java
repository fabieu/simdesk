package de.sustineo.acc.servertools.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.WebStorage;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoIcon;
import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.configuration.Reference;
import de.sustineo.acc.servertools.configuration.VaadinConfiguration;
import de.sustineo.acc.servertools.entities.auth.UserPrincipal;
import de.sustineo.acc.servertools.services.auth.SecurityService;
import de.sustineo.acc.servertools.utils.ApplicationContextProvider;
import de.sustineo.acc.servertools.views.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;

import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;


@PageTitle(VaadinConfiguration.APPLICATION_NAME)
@StyleSheet(value = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap-grid.min.css")
public class MainLayout extends AppLayout {
    private static final String DEFAULT_THEME = Lumo.DARK;
    private static final String SESSION_ATTRIBUTE_THEME = "vaadin.custom.theme";

    private final SecurityService securityService;
    private final BuildProperties buildProperties;
    private final String privacyUrl;
    private final String impressumUrl;
    private static final LinkedHashMap<String, Tabs> menuMap = new LinkedHashMap<>();
    private H1 viewTitle;

    public MainLayout(SecurityService securityService,
                      ApplicationContextProvider applicationContextProvider,
                      @Value("${leaderboard.links.privacy}") String privacyUrl,
                      @Value("${leaderboard.links.impressum}") String impressumUrl) {
        this.securityService = securityService;
        this.buildProperties = applicationContextProvider.getBean(BuildProperties.class);
        this.privacyUrl = privacyUrl;
        this.impressumUrl = impressumUrl;


        setPrimarySection(Section.NAVBAR);
        addToNavbar(false, createNavbarContent(), createNavbarButtons());

        createMenuTabs();
        addToDrawer(createDrawerContent());
        setDrawerOpened(false); // Set drawerOpened to ensure smooth animation, will be overridden

        // Read and apply session attributes
        WebStorage.getItem(WebStorage.Storage.LOCAL_STORAGE, SESSION_ATTRIBUTE_THEME, value -> {
            setTheme(Optional.ofNullable(value).orElse(DEFAULT_THEME));
        });
    }

    private void createMenuTabs() {
        menuMap.put("main", createMenuTabs(createDefaultMenuTabs()));

        if (ProfileManager.isLeaderboardProfileEnabled()) {
            menuMap.put("leaderboard", createMenuTabs(MainLayout.createLeaderboardMenuTabs()));
        }

        if (ProfileManager.isEntrylistProfileEnabled()) {
            menuMap.put("entrylist", createMenuTabs(MainLayout.createEntrylistMenuTabs()));
        }

        if (ProfileManager.isBopProfileEnabled()) {
            menuMap.put("bop", createMenuTabs(MainLayout.createBopMenuTabs()));
        }

        menuMap.put("external links", createMenuTabs(MainLayout.createExternalMenuTabs()));
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

        layout.add(createUserDetails(), createHomeButton(), createThemeToggleButton());

        return layout;
    }

    private Component createUserDetails() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        Optional<UserPrincipal> user = securityService.getAuthenticatedUser();
        if (user.isPresent()) {
            Icon userIcon = VaadinIcon.USER.create();

            Span userName = new Span(user.get().getUsername());
            userName.getStyle()
                    .setFontWeight(Style.FontWeight.BOLD);

            Button authenticationButton = new Button("Logout");
            authenticationButton.addClickListener(event -> securityService.logout());
            layout.add(userIcon, userName, authenticationButton);
        } else {
            Button authenticationButton = new Button("Login");
            authenticationButton.addClickListener(event -> authenticationButton.getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
            layout.add(authenticationButton);
        }

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
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        themeList.removeAll(List.of(Lumo.DARK, Lumo.LIGHT));
        themeList.add(theme);

        WebStorage.setItem(WebStorage.Storage.LOCAL_STORAGE, SESSION_ATTRIBUTE_THEME, theme);
    }

    private String getTheme() {
        return UI.getCurrent().getElement().getThemeList().stream()
                .findFirst()
                .orElse(DEFAULT_THEME);
    }

    private Component createDrawerContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);
        layout.getStyle()
                .setPadding("var(--lumo-space-m) 0");

        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.setPadding(false);
        menuLayout.setSpacing(false);

        int menuMapCounter = 0;
        for (SortedMap.Entry<String, Tabs> entry : menuMap.entrySet()) {
            menuLayout.add(createMenuHeader(entry.getKey()));
            menuLayout.add(entry.getValue());

            if (menuMapCounter < menuMap.size() - 1) {
                menuLayout.add(ComponentUtils.createSpacer());
            }

            menuMapCounter++;
        }

        Component infoLayout = createDrawerFooter();

        layout.addAndExpand(menuLayout);
        layout.add(infoLayout);

        return layout;
    }

    private Component createDrawerFooter() {
        VerticalLayout footerLayout = new VerticalLayout();
        footerLayout.setPadding(false);

        // Layout for custom links
        VerticalLayout linkLayout = getDrawerLayout();

        if (impressumUrl != null && !impressumUrl.isEmpty()) {
            linkLayout.add(new Anchor(impressumUrl, "Impressum", AnchorTarget.BLANK));
        }

        if (privacyUrl != null && !privacyUrl.isEmpty()) {
            linkLayout.add(new Anchor(privacyUrl, "Privacy policy", AnchorTarget.BLANK));
        }

        // Layout for creator information
        VerticalLayout creatorLayout = getDrawerLayout();

        Div creatorContainer = new Div();
        creatorContainer.add(new Text("Made with ❤️ by "));
        creatorContainer.add(new Anchor(Reference.SUSTINEO, "Fabian Eulitz", AnchorTarget.BLANK));

        Span copyright = new Span("Copyright © 2022 - " + Year.now().getValue());

        creatorLayout.add(creatorContainer, copyright);

        // Layout for build information
        VerticalLayout buildLayout = getDrawerLayout();

        Span version = new Span("Version " + buildProperties.getVersion());
        version.getElement().getThemeList().add("badge");

        buildLayout.add(version);

        // Combine layouts
        footerLayout.add(linkLayout, creatorLayout, buildLayout);
        return footerLayout;
    }

    private static VerticalLayout getDrawerLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.getThemeList().add("spacing-xs");
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        return layout;
    }

    private Component createMenuHeader(String title) {
        Span span = new Span(title.toUpperCase());
        span.setWidthFull();
        span.getStyle()
                .setTextAlign(Style.TextAlign.CENTER)
                .set("font-weight", "bold");

        return span;
    }

    private Tabs createMenuTabs(Tab[] tabEntries) {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(tabEntries);
        return tabs;
    }

    private static Tab[] createDefaultMenuTabs() {
        return new Tab[]{
                createTab("Home", VaadinIcon.HOME.create(), MainView.class),
                createTab("Converter", VaadinIcon.REFRESH.create(), ConverterView.class),
        };
    }

    public static Tab[] createLeaderboardMenuTabs() {
        return new Tab[]{
                createTab("Lap Times", VaadinIcon.CLOCK.create(), OverallLapTimesView.class),
                createTab("Sessions", LumoIcon.ORDERED_LIST.create(), SessionView.class),
                createTab("Drivers", VaadinIcon.USERS.create(), DriverView.class),
        };
    }

    public static Tab[] createEntrylistMenuTabs() {
        return new Tab[]{
                createTab("Entrylist Validation", VaadinIcon.COG.create(), EntrylistValidatorView.class),
        };
    }

    public static Tab[] createBopMenuTabs() {
        return new Tab[]{
                createTab("BoP Editor", VaadinIcon.SCALE.create(), BopEditorView.class),
        };
    }

    private static Tab[] createExternalMenuTabs() {
        return new Tab[]{
                createExternalTab("Feedback", VaadinIcon.COMMENT.create(), Reference.FEEDBACK),
        };
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

        final Tab tab = new Tab();
        tab.add(icon, link);
        tab.setId("tab-" + text.toLowerCase());
        tab.setSelected(false);

        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();

        if (getContent() != null) {
            for (Tabs menu : menuMap.values()) {
                menu.getChildren()
                        .filter(tab -> getContent().getClass().equals(ComponentUtil.getData(tab, Class.class)))
                        .findFirst()
                        .map(Tab.class::cast)
                        .ifPresentOrElse(menu::setSelectedTab, () -> menu.setSelectedTab(null));
            }
            viewTitle.setText(getCurrentPageTitle());
        } else {
            for (Tabs menu : menuMap.values()) {
                menu.setSelectedTab(null);
            }
            viewTitle.setText(VaadinConfiguration.APPLICATION_NAME);
        }

        // Close drawer when navigating to different view
        setDrawerOpened(false);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }

}
