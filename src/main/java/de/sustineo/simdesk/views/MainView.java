package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.configuration.VaadinConfiguration;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import de.sustineo.simdesk.utils.FormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "/home", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME)
@AnonymousAllowed
public class MainView extends VerticalLayout {
    private final Optional<SessionService> sessionService;
    private static final Integer RECENT_SESSION_DAYS = 7;
    private final String communityName;


    public MainView(@Autowired(required = false) SessionService sessionService,
                    @Value("${simdesk.community.name}") String communityName) {
        this.sessionService = Optional.ofNullable(sessionService);
        this.communityName = communityName;

        setSizeFull();
        setPadding(false);

        add(createHeader());
        add(createNavigationTabs());

        addAndExpand(createMainContent());
    }

    private Component createHeader() {
        Div header = new Div();
        header.setId("home-header");
        String communityName = Optional.ofNullable(this.communityName)
                .map(name -> "SimDesk by " + name)
                .orElse("SimDesk");
        header.add(new H1(communityName));
        return header;
    }

    private Component createNavigationTabs() {
        List<Tab> tabs = new ArrayList<>();
        if (ProfileManager.isLeaderboardProfileEnabled()) {
            tabs.addAll(Arrays.stream(MainLayout.createLeaderboardMenuTabs()).toList());
        }

        if (ProfileManager.isEntrylistProfileEnabled()) {
            tabs.addAll(Arrays.stream(MainLayout.createEntrylistMenuTabs()).toList());
        }

        if (ProfileManager.isBopProfileEnabled()) {
            tabs.addAll(Arrays.stream(MainLayout.createBopMenuTabs()).toList());
        }

        // Add custom styling to navigation tabs
        List<Div> tabDivs = new ArrayList<>();
        for (Tab tab : tabs) {
            Div tabDiv = new Div();
            tabDiv.addClassNames("col-12", "col-md-6", "col-lg-3");
            tabDiv.add(tab);

            tabDivs.add(tabDiv);
        }

        Div container = new Div();
        container.addClassNames("container-fluid");

        final Div row = new Div();
        row.setId("home-tabs");
        row.addClassNames("row", "g-3");
        row.setWidthFull();
        row.add(tabDivs.toArray(new Div[0]));
        container.add(row);

        return container;
    }

    private Component createMainContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.getStyle().set("padding-bottom", "3rem");

        if (ProfileManager.isLeaderboardProfileEnabled()) {
            layout.add(new H3(String.format("Recent sessions (last %s days)", RECENT_SESSION_DAYS)));
            Optional<Component> sessionGrid = createSessionGrid(RECENT_SESSION_DAYS);
            if (sessionGrid.isPresent()) {
                layout.add(sessionGrid.get());
            } else {
                layout.add(new Paragraph("No sessions found"));
            }
        }

        return layout;
    }

    private Optional<Component> createSessionGrid(int recentDays) {
        if (sessionService.isEmpty()) {
            return Optional.empty();
        }

        List<Session> sessions = sessionService.get().getRecentSessions(recentDays);
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