package de.sustineo.acc.servertools.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.servertools.configuration.EnvironmentConfiguration;
import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.configuration.VaadinConfiguration;
import de.sustineo.acc.servertools.entities.Session;
import de.sustineo.acc.servertools.entities.Stats;
import de.sustineo.acc.servertools.layouts.MainLayout;
import de.sustineo.acc.servertools.services.leaderboard.SessionService;
import de.sustineo.acc.servertools.services.leaderboard.StatsService;
import de.sustineo.acc.servertools.utils.FormatUtils;

import java.util.List;
import java.util.Optional;

@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "/home", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME)
@AnonymousAllowed
public class MainView extends VerticalLayout {
    private final SessionService sessionService;
    private final StatsService statsService;

    public MainView(ComponentUtils componentUtils, SessionService sessionService, StatsService statsService) {
        this.sessionService = sessionService;
        this.statsService = statsService;

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

        tabs.add(MainLayout.createLeaderboardMenuTabs());
        tabs.add(MainLayout.createEntrylistMenuTabs());

        return tabs;
    }

    private Component createMainContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.getStyle().set("padding-bottom", "3rem");

        if (ProfileManager.isLeaderboardProfileEnabled()) {
            layout.add(new H3("Recent sessions (last 7 days)"));
            Optional<Component> sessionGrid = createSessionGrid(7);
            if (sessionGrid.isPresent()) {
                layout.add(sessionGrid.get());
            } else {
                layout.add(new Paragraph("No sessions found"));
            }

            layout.add(new H3("Leaderboard stats"));
            layout.add(createLeaderboardStatsContainer());
        }

        return layout;
    }

    private Component createLeaderboardStatsContainer() {
        Div row = new Div();
        row.setClassName("row");
        row.add(createStatsBoxes());

        Div container = new Div();
        container.setId("home-stats");
        container.setClassName("container-fluid");
        container.setWidthFull();

        container.add(row);

        return container;
    }

    private Div[] createStatsBoxes() {
        Stats stats = statsService.getStats();

        return new Div[]{
                createStatsBox("Total sessions", stats.getTotalSessions()),
                createStatsBox("Total laps", stats.getTotalLaps()),
                createStatsBox("Unique drivers", stats.getTotalDrivers()),
        };
    }

    private static Div createStatsBox(String title, String value) {
        Div statsBox = new Div();
        statsBox.addClassNames("stats-box", "noselect", "col-12", "col-md-6", "col-lg-3");

        Paragraph titleElement = new Paragraph(title);
        titleElement.addClassName("stats-title");

        Paragraph valueElement = new Paragraph(value);
        valueElement.addClassName("stats-value");

        statsBox.add(titleElement, valueElement);
        return statsBox;
    }

    private Optional<Component> createSessionGrid(int recentDays) {
        List<Session> sessions = sessionService.getRecentSessions(recentDays);
        if (sessions.isEmpty()) {
            return Optional.empty();
        }

        Grid<Session> grid = new Grid<>(Session.class, false);
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

        grid.setItems(sessions);
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        SingleSelect<Grid<Session>, Session> singleSelect = grid.asSingleSelect();
        singleSelect.addValueChangeListener(e -> {
            Session selectedSession = e.getValue();

            if (selectedSession != null) {
                getUI().ifPresent(ui -> ui.navigate(SessionRankingView.class,
                        new RouteParameters(
                                new RouteParam(SessionRankingView.ROUTE_PARAMETER_FILE_CHECKSUM, selectedSession.getFileChecksum())
                        )
                ));
            }
        });

        return Optional.of(grid);
    }
}
