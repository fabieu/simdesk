package de.sustineo.simdesk.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.Lumo;
import de.sustineo.simdesk.configuration.Reference;
import de.sustineo.simdesk.entities.auth.UserPrincipal;
import de.sustineo.simdesk.entities.auth.UserRoleEnum;
import de.sustineo.simdesk.entities.menu.*;
import de.sustineo.simdesk.services.MenuService;
import de.sustineo.simdesk.services.ThemeService;
import de.sustineo.simdesk.services.auth.SecurityService;
import de.sustineo.simdesk.views.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;

import java.util.*;

@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {
    private final ThemeService themeService;
    private final SecurityService securityService;
    private final MenuService menuService;
    private final BuildProperties buildProperties;

    private final String privacyUrl;
    private final String impressumUrl;
    private final LinkedHashMap<MenuEntityCategory, Tabs> menuMap = new LinkedHashMap<>();

    public MainLayout(ThemeService themeService,
                      SecurityService securityService,
                      MenuService menuService,
                      BuildProperties buildProperties,
                      @Value("${simdesk.links.privacy}") String privacyUrl,
                      @Value("${simdesk.links.impressum}") String impressumUrl) {
        this.securityService = securityService;
        this.menuService = menuService;
        this.themeService = themeService;
        this.buildProperties = buildProperties;
        this.privacyUrl = privacyUrl;
        this.impressumUrl = impressumUrl;

        themeService.init();

        setPrimarySection(Section.NAVBAR);
        addToNavbar(false, createNavbarContent());

        createMenuTabs();

        addToDrawer(createDrawerContent());
        setDrawerOpened(false); // Set drawerOpened to ensure smooth animation, will be overridden
    }

    private void createMenuTabs() {
        Map<MenuEntityCategory, List<MenuEntity>> menuItemMap = menuService.getItemsByCategory();

        for (Map.Entry<MenuEntityCategory, List<MenuEntity>> entry : menuItemMap.entrySet()) {
            final Tabs tabs = new Tabs();
            tabs.setOrientation(Tabs.Orientation.VERTICAL);

            for (MenuEntity item : entry.getValue()) {
                NavigationTarget navigationTarget = item.getNavigationTarget();

                Tab tab = new Tab();
                tab.add(item.getIcon(), navigationTarget.asComponent(item.getName()));

                if (navigationTarget instanceof InternalLink(Class<? extends Component> target)) {
                    ComponentUtil.setData(tab, Class.class, target); // important for setting selected tab
                } else if (navigationTarget instanceof ExternalLink) {
                    tab.setSelected(false);
                }

                tabs.add(tab);
            }

            menuMap.put(entry.getKey(), tabs);
        }
    }

    private Component createNavbarContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(createNavbarNavigation(), createNavbarMenu());
        return layout;
    }

    private Component createNavbarNavigation() {
        HorizontalLayout layout = new HorizontalLayout();

        Div logo = new Div();
        logo.setId("navbar-logo");

        RouterLink logoRouter = new RouterLink(MainView.class);
        logoRouter.add(logo);

        layout.add(new DrawerToggle(), logoRouter);
        return layout;
    }

    private MenuBar createNavbarMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        menuBar.getStyle()
                .setMargin("0");
        return menuBar;
    }

    private Component createNavbarMenu() {
        MenuBar userMenuBar = createNavbarMenuBar();
        addThemeSwitcher(userMenuBar);
        addUserMenu(userMenuBar);

        HorizontalLayout menuBarLayout = new HorizontalLayout(userMenuBar);
        menuBarLayout.setSpacing(false);
        menuBarLayout.getStyle()
                .setMarginRight("var(--lumo-space-m)");

        return menuBarLayout;
    }

    private void addThemeSwitcher(MenuBar menuBar) {
        String currentLumoTheme = themeService.getCurrentLumoTheme();

        FontIcon darkThemeIcon = new FontIcon("fa-regular", "fa-moon");
        FontIcon lightThemeIcon = new FontIcon("fa-regular", "fa-sun");

        String darkThemeLabel = "Enable dark theme";
        String lightThemeLabel = "Enable light theme";

        FontIcon themeButtonIcon = Lumo.DARK.equals(currentLumoTheme) ? lightThemeIcon : darkThemeIcon;
        String label = Lumo.DARK.equals(currentLumoTheme) ? lightThemeLabel : darkThemeLabel;

        Button themeSwitchButton = new Button(themeButtonIcon);
        themeSwitchButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        themeSwitchButton.setTooltipText(label);
        themeSwitchButton.setAriaLabel(label);
        themeSwitchButton.addClickListener(e -> {
            if (Lumo.DARK.equals(themeService.getCurrentLumoTheme())) {
                themeService.setLumoTheme(Lumo.LIGHT);
                themeSwitchButton.setIcon(darkThemeIcon);
                themeSwitchButton.setTooltipText(darkThemeLabel);
                themeSwitchButton.setAriaLabel(darkThemeLabel);
            } else {
                themeService.setLumoTheme(Lumo.DARK);
                themeSwitchButton.setIcon(lightThemeIcon);
                themeSwitchButton.setTooltipText(lightThemeLabel);
                themeSwitchButton.setAriaLabel(lightThemeLabel);
            }
        });

        menuBar.addItem(themeSwitchButton);
    }

    private void addUserMenu(MenuBar menuBar) {
        Optional<UserPrincipal> user = securityService.getAuthenticatedUserPrincipal();

        Avatar avatar = new Avatar();
        avatar.setTooltipEnabled(true);

        MenuItem avatarMenuItem = menuBar.addItem(avatar);
        SubMenu userSubMenu = avatarMenuItem.getSubMenu();

        if (user.isPresent()) {
            avatar.setName(user.get().getUsername());

            Optional<String> avatarUrl = securityService.getAvatarUrl();
            avatarUrl.ifPresent(avatar::setImage);

            MenuItem profileMenuItem = userSubMenu.addItem("Profile");
            profileMenuItem.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(UserProfileView.class)));

            if (securityService.hasAnyAuthority(UserRoleEnum.ROLE_ADMIN)) {
                MenuItem settingsMenuItem = userSubMenu.addItem("Settings");
                settingsMenuItem.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(SettingsView.class)));
            }

            Span logoutText = new Span("Logout");
            logoutText.getStyle()
                    .setColor("var(--lumo-error-text-color)");

            MenuItem logoutMenuItem = userSubMenu.addItem(logoutText);
            logoutMenuItem.addClickListener(event -> securityService.logout());
        } else {
            MenuItem loginMenuItem = userSubMenu.addItem("Login");
            loginMenuItem.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
        }
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
        for (SortedMap.Entry<MenuEntityCategory, Tabs> entry : menuMap.entrySet()) {
            menuLayout.add(createMenuHeader(entry.getKey().getName()));
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
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(createDrawerFooterLinkLayout(), createDrawerFooterVersionLayout());
        return layout;
    }

    private Component createDrawerFooterLinkLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        if (impressumUrl != null && !impressumUrl.isEmpty()) {
            layout.add(new Anchor(impressumUrl, "Impressum", AnchorTarget.BLANK));
        }

        if (privacyUrl != null && !privacyUrl.isEmpty()) {
            layout.add(new Anchor(privacyUrl, "Privacy policy", AnchorTarget.BLANK));
        }

        layout.add(new Anchor(Reference.SIMDESK_CREDITS, "Credits", AnchorTarget.BLANK));

        return layout;
    }

    private Component createDrawerFooterVersionLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setPadding(true);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Version badge + reference to GitHub release
        Span versionBadge = new Span("Version " + buildProperties.getVersion());
        versionBadge.getElement().getThemeList().add("badge contrast");

        FontIcon githubIcon = new FontIcon("fa-brands", "fa-github");
        githubIcon.getStyle()
                .setMargin("0 var(--lumo-space-s)")
                .setFontSize("var(--lumo-font-size-s)")
                .setColor("var(--lumo-body-text-color)");

        Anchor versionAnchor = new Anchor(Reference.GITHUB_RELEASES);
        versionAnchor.add(githubIcon, versionBadge);
        versionAnchor.setTarget(AnchorTarget.BLANK);

        layout.add(versionAnchor);

        return layout;
    }

    private Component createMenuHeader(String title) {
        Span span = new Span(title);
        span.setWidthFull();
        span.getStyle()
                .setTextAlign(Style.TextAlign.CENTER)
                .set("font-weight", "bold");

        return span;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();

        if (getContent() != null) {
            for (Tabs tabs : menuMap.values()) {
                tabs.getChildren()
                        .filter(tab -> getContent().getClass().equals(ComponentUtil.getData(tab, Class.class)))
                        .findFirst()
                        .map(Tab.class::cast)
                        .ifPresentOrElse(tabs::setSelectedTab, () -> tabs.setSelectedTab(null));
            }
        } else {
            for (Tabs tabs : menuMap.values()) {
                tabs.setSelectedTab(null);
            }
        }

        // Close drawer when navigating to different view
        setDrawerOpened(false);
    }
}
