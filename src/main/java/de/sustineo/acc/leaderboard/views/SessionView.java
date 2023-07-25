package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.ProfileManager;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.Session;
import de.sustineo.acc.leaderboard.layouts.MainLayout;
import de.sustineo.acc.leaderboard.services.leaderboard.SessionService;
import de.sustineo.acc.leaderboard.utils.FormatUtils;
import de.sustineo.acc.leaderboard.views.filter.FilterUtils;
import de.sustineo.acc.leaderboard.views.filter.SessionFilter;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/sessions", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Sessions")
@AnonymousAllowed
public class SessionView extends VerticalLayout {
    private final SessionService sessionService;

    public SessionView(SessionService sessionService, ComponentUtils componentUtils) {
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(false);

        addAndExpand(createSessionGrid());
        add(componentUtils.createFooter());
    }

    private Component createSessionGrid() {
        List<Session> sessions = sessionService.getAllSessions();

        Grid<Session> grid = new Grid<>(Session.class, false);
        Grid.Column<Session> weatherColumn = grid.addComponentColumn(ComponentUtils::createWeatherIcon)
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Session> sessionDatetimeColumn = grid.addColumn(session -> FormatUtils.formatDatetime(session.getSessionDatetime()))
                .setHeader("Session Time")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Session::getSessionDatetime);
        Grid.Column<Session> serverNameColumn = grid.addColumn(Session::getServerName)
                .setHeader("Server Name")
                .setSortable(true);
        Grid.Column<Session> trackNameColumn = grid.addColumn(Session::getTrackName)
                .setHeader("Track Name")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Session> sessionTypeColumn = grid.addColumn(session -> session.getSessionType().getDescription())
                .setHeader("Session")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        Grid.Column<Session> carCountColumn = grid.addColumn(Session::getCarCount)
                .setHeader("Cars")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);

        GridListDataView<Session> dataView = grid.setItems(sessions);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        SessionFilter sessionFilter = new SessionFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(serverNameColumn).setComponent(FilterUtils.createFilterHeader(sessionFilter::setServerName));
        headerRow.getCell(trackNameColumn).setComponent(FilterUtils.createFilterHeader(sessionFilter::setTrackName));
        headerRow.getCell(sessionTypeColumn).setComponent(FilterUtils.createFilterHeader(sessionFilter::setSessionDescription));

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
