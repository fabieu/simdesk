package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import org.springframework.boot.info.BuildProperties;

@Route(value = "/dashboard", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME)
public class DashboardView extends VerticalLayout {

    public DashboardView(BuildProperties buildProperties) {
        addClassName("dashboard-view");
        setSizeFull();

        addAndExpand(createMainContent());
        add(MainView.createFooterContent(buildProperties));
    }

    private Component createMainContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.addAndExpand(new H1("Welcome to ACC Leaderboard"));

        return layout;
    }
}
