package de.sustineo.acc.leaderboard.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.views.DriverView;
import de.sustineo.acc.leaderboard.views.MainView;
import de.sustineo.acc.leaderboard.views.OverallLapTimesView;
import de.sustineo.acc.leaderboard.views.SessionView;

import java.util.Optional;


@PageTitle(VaadinConfiguration.APPLICATION_NAME)
public class MainLayout extends AppLayout implements RouterLayout {
    private final Tabs menu;
    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.NAVBAR);
        addToNavbar(true, createNavbarContent(), createNavbarButtons());

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

    public static Tab[] createMenuTabs() {
        return new Tab[]{
                createTab("Home", MainView.class),
                createTab("Lap Times", OverallLapTimesView.class),
                createTab("Sessions", SessionView.class),
                createTab("Drivers", DriverView.class),
        };
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        tab.setId("tab-" + text.toLowerCase());
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();

        if (getContent() != null) {
            // Select the tab corresponding to currently shown view
            getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
            viewTitle.setText(getCurrentPageTitle());
        } else {
            menu.setSelectedTab(null);
            viewTitle.setText(VaadinConfiguration.APPLICATION_NAME);
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
