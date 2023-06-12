package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.layouts.MainLayout;

@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "/dashboard", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME)
public class MainView extends VerticalLayout {

    public MainView() {
        addClassName("dashboard-view");
        setSizeFull();

        addAndExpand(createMainContent());
        add(ComponentUtils.createFooter());
    }

    private Component createMainContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.addAndExpand(new H1("Welcome to ACC Leaderboard"));

        return layout;
    }
}
