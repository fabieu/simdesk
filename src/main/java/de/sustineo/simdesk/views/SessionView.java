package de.sustineo.simdesk.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.configuration.VaadinConfiguration;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.layouts.MainLayout;
import de.sustineo.simdesk.services.leaderboard.SessionService;
import de.sustineo.simdesk.utils.FormatUtils;
import de.sustineo.simdesk.views.enums.TimeRange;
import de.sustineo.simdesk.views.filter.GridFilter;
import de.sustineo.simdesk.views.filter.SessionFilter;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Route(value = "/leaderboard/sessions", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Leaderboard - Sessions")
@AnonymousAllowed
public class SessionView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver {
    private static final String QUERY_PARAMETER_TIME_RANGE = "timeRange";
    private final SessionService sessionService;

    private Grid<Session> sessionGrid;
    private TimeRange timeRange = TimeRange.LAST_MONTH;

    public SessionView(SessionService sessionService) {
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final QueryParameters queryParameters = event.getLocation().getQueryParameters();

        Optional<String> timeRange = queryParameters.getSingleParameter(QUERY_PARAMETER_TIME_RANGE);
        if (timeRange.isPresent() && EnumUtils.isValidEnumIgnoreCase(TimeRange.class, timeRange.get())) {
            this.timeRange = EnumUtils.getEnumIgnoreCase(TimeRange.class, timeRange.get());
        }

        this.sessionGrid = createSessionGrid(this.timeRange);
        add(createSessionHeader(this.timeRange));
        addAndExpand(sessionGrid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateQueryParameters(this.timeRange);
    }

    private Component createSessionHeader(TimeRange timeRange) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setPadding(true);
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // Header displaying the car group and track name
        H3 heading = new H3("Sessions");

        Select<TimeRange> timeRangeSelect = new Select<>();
        timeRangeSelect.setItems(TimeRange.values());
        timeRangeSelect.setValue(timeRange);
        timeRangeSelect.addComponents(TimeRange.LAST_WEEK, ComponentUtils.createSpacer());
        timeRangeSelect.setItemLabelGenerator(TimeRange::getDescription);
        timeRangeSelect.addValueChangeListener(event -> {
            replaceSessionGrid(event.getValue());
            updateQueryParameters(event.getValue());
        });

        layout.add(heading, timeRangeSelect);
        return layout;
    }

    private Grid<Session> createSessionGrid(TimeRange timeRange) {
        List<Session> sessions = sessionService.getAllSessions(timeRange);

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
        grid.setSizeFull();
        grid.setColumnReorderingAllowed(true);
        grid.setMultiSort(true, true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        SessionFilter sessionFilter = new SessionFilter(dataView);
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(serverNameColumn).setComponent(GridFilter.createHeader(sessionFilter::setServerName));
        headerRow.getCell(trackNameColumn).setComponent(GridFilter.createHeader(sessionFilter::setTrackName));
        headerRow.getCell(sessionTypeColumn).setComponent(GridFilter.createHeader(sessionFilter::setSessionDescription));

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

        return grid;
    }

    private void replaceSessionGrid(TimeRange timeRange) {
        Grid<Session> grid = createSessionGrid(timeRange);
        replace(this.sessionGrid, grid);
        this.sessionGrid = grid;
    }

    private void updateQueryParameters(TimeRange timeRange) {
        String deepLinkingUrl = RouteConfiguration.forSessionScope().getUrl(getClass());
        // Assign the full deep linking URL directly using
        // History object: changes the URL in the browser,
        // but doesn't reload the page.
        String deepLinkingUrlWithParam = UriComponentsBuilder.fromPath(deepLinkingUrl)
                .queryParam(QUERY_PARAMETER_TIME_RANGE, timeRange.name().toLowerCase())
                .toUriString();
        getUI().ifPresent(ui -> ui.getPage().getHistory().replaceState(null, deepLinkingUrlWithParam));
    }
}
