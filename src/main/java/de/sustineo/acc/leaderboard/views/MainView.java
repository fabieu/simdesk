package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.EnvironmentConfiguration;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.Session;
import de.sustineo.acc.leaderboard.layouts.MainLayout;
import de.sustineo.acc.leaderboard.services.SessionService;
import de.sustineo.acc.leaderboard.utils.FormatUtils;

import java.util.Arrays;
import java.util.List;

@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "/home", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME)
@AnonymousAllowed
public class MainView extends VerticalLayout {
    private final SessionService sessionService;

    public MainView(ComponentUtils componentUtils, SessionService sessionService) {
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(false);

        add(createHeader());
        add(createNavigationTabs());
        addAndExpand(createMainContent());
        add(componentUtils.createFooter());
    }

    private Component createHeader() {
        Div header = new Div();
        header.setId("home-header");
        header.add(new H1(EnvironmentConfiguration.getCommunityName() + " Server Tools"));
        return header;
    }

    private Component createNavigationTabs() {
        final Tabs tabs = new Tabs();
        tabs.setId("home-tabs");
        tabs.setWidthFull();
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);

        Tab[] filteredTabArray = Arrays.stream(MainLayout.createLeaderboardMenuTabs())
                .filter(tab -> !"tab-home".equals(tab.getId().orElse(null)))
                .toArray(Tab[]::new);
        tabs.add(filteredTabArray);

        return tabs;
    }

    private Component createMainContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.add(new H3("Latest sessions"));
        layout.add(createSessionGrid());

        return layout;
    }

    private Component createSessionGrid() {
        final int MAX_SESSIONS = 10;

        Grid<Session> grid = new Grid<>(Session.class, false);

        List<Session> sessions = sessionService.getAllSessions();
        List<Session> truncatedSessions = sessions.stream().limit(MAX_SESSIONS).toList();
        grid.setItems(truncatedSessions);

        grid.addComponentColumn(ComponentUtils::createWeatherIcon)
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(session -> FormatUtils.formatDatetime(session.getSessionDatetime()))
                .setHeader("Session Time")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(Session::getServerName)
                .setHeader("Server Name");
        grid.addColumn(Session::getTrackName)
                .setHeader("Track Name")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(session -> session.getSessionType().getDescription())
                .setHeader("Session")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(Session::getCarCount)
                .setHeader("Cars")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        SingleSelect<Grid<Session>, Session> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
            Session selectedSession = e.getValue();

            if (selectedSession != null) {
                getUI().ifPresent(ui -> ui.navigate(SessionRankingView.class,
                        new RouteParameters(
                                new RouteParam(SessionRankingView.ROUTE_PARAMETER_SESSION_ID, selectedSession.getId().toString())
                        )
                ));
            }
        });

        return grid;
    }
}
