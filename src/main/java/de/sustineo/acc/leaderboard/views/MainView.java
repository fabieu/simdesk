package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
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
import org.springframework.boot.info.BuildProperties;

import java.time.LocalDate;
import java.util.Optional;


@PageTitle(VaadinConfiguration.APPLICATION_NAME)
public class MainView extends AppLayout implements RouterLayout {
    private final Tabs menu;
    private H1 viewTitle;

    public MainView() {
        setPrimarySection(Section.NAVBAR);
        addToNavbar(true, createNavbarContent(), createThemeToggleButton());

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
                createTab("Dashboard", DashboardView.class),
                createTab("All Time Ranking", AllTimeGroupRankingView.class),
                createTab("Drivers", DriverView.class),
        };
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    private Button createThemeToggleButton() {
        return new Button("Toggle theme variant", click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });
    }

    public static Component createFooterContent(BuildProperties buildProperties) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.add(new Text("Made with ❤️ by Fabian Eulitz - © " + LocalDate.now().getYear() + " - Version " + buildProperties.getVersion()));
        return layout;
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