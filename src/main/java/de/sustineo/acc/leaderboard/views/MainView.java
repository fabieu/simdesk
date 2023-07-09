package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.Environment;
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

        initializeDevelopmentNotification();
    }

    private Component createHeader() {
        Div header = new Div();
        header.setId("home-header");
        header.add(new H1(Environment.COMMUNITY_NAME + " Leaderboard"));
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
                .filter(tab -> !List.of("tab-home").contains(tab.getId().orElse(null)))
                .toArray(Tab[]::new);
        tabs.add(filteredTabArray);

        return tabs;
    }

    private Component createMainContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.add(new H3("Latest Sessions"));
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

    private void initializeDevelopmentNotification() {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        notification.setPosition(Notification.Position.BOTTOM_STRETCH);
        notification.setDuration(5000); // 5 seconds

        Icon icon = VaadinIcon.WARNING.create();
        Div text = new Div(new Text(VaadinConfiguration.APPLICATION_NAME + " is currently under development."));

        Button closeButton = new Button(VaadinIcon.CLOSE.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(icon, text, closeButton);
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }
}
