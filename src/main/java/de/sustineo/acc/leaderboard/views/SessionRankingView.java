package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.Session;
import de.sustineo.acc.leaderboard.entities.comparator.SessionRankingLapTimeComparator;
import de.sustineo.acc.leaderboard.entities.enums.SessionType;
import de.sustineo.acc.leaderboard.entities.ranking.SessionRanking;
import de.sustineo.acc.leaderboard.layouts.MainLayout;
import de.sustineo.acc.leaderboard.services.RankingService;
import de.sustineo.acc.leaderboard.services.SessionService;
import de.sustineo.acc.leaderboard.utils.FormatUtils;
import de.sustineo.acc.leaderboard.views.generators.SessionRankingDNFNameGenerator;
import de.sustineo.acc.leaderboard.views.generators.SessionRankingPodiumPartNameGenerator;
import de.sustineo.acc.leaderboard.views.renderers.ranking.RankingRenderer;
import de.sustineo.acc.leaderboard.views.renderers.ranking.SessionRankingRenderer;

import java.util.List;

@Route(value = "sessions/:sessionId", layout = MainLayout.class)
@PageTitle(VaadinConfiguration.APPLICATION_NAME_PREFIX + "Session")
@AnonymousAllowed
public class SessionRankingView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE_PARAMETER_SESSION_ID = "sessionId";
    private final RankingService rankingService;
    private final SessionService sessionService;
    private final ComponentUtils componentUtils;

    public SessionRankingView(RankingService rankingService, SessionService sessionService, ComponentUtils componentUtils) {
        this.rankingService = rankingService;
        this.sessionService = sessionService;
        this.componentUtils = componentUtils;
        setSizeFull();
    }


    private Component createSessionInformation(Integer sessionId) {
        Session session = sessionService.getSession(sessionId);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);

        H3 heading = new H3();
        heading.setText(String.format("%s - %s - %s", session.getSessionType().getDescription(), session.getTrackName(), session.getServerName()));

        Icon weatherIcon = ComponentUtils.getWeatherIcon(session);

        Span sessionDatetimeBadge = new Span();
        sessionDatetimeBadge.setText(FormatUtils.formatDatetime(session.getSessionDatetime()));
        sessionDatetimeBadge.getElement().getThemeList().add("badge contrast");

        layout.add(weatherIcon, heading, sessionDatetimeBadge);

        return layout;
    }

    private Component createLeaderboardGrid(Integer sessionId) {
        List<SessionRanking> sessionRankings = rankingService.getSessionRanking(sessionId);
        List<SessionRanking> filteredSessionRankings = sessionRankings.stream()
                .filter(sessionRanking -> sessionRanking.getLapCount() > 0)
                .toList();
        SessionRanking bestTotalTimeSessionRanking = sessionRankings.stream().findFirst().orElse(new SessionRanking());
        SessionRanking bestLapTimeSessionRanking = sessionRankings.stream()
                .filter(sessionRanking -> sessionRanking.getBestLapTimeMillis() > 0)
                .min(new SessionRankingLapTimeComparator())
                .orElse(new SessionRanking());

        Grid<SessionRanking> grid = new Grid<>(SessionRanking.class, false);
        grid.addColumn(SessionRanking::getRanking)
                .setHeader("#")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setPartNameGenerator(new SessionRankingPodiumPartNameGenerator());
        grid.addColumn(SessionRankingRenderer.createRaceNumberRenderer())
                .setHeader("Race Number")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(SessionRanking::getRaceNumber);
        grid.addColumn(SessionRanking::getCarGroup)
                .setHeader("Car Group")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(SessionRanking::getCarModelName)
                .setHeader("Car Model")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        grid.addColumn(SessionRankingRenderer.createDriversRenderer())
                .setHeader("Drivers")
                .setSortable(true);
        grid.addColumn(sessionRanking -> sessionRanking.getLapCount() > 0 ? sessionRanking.getLapCount() : RankingRenderer.DNS)
                .setHeader("Laps")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);
        if (SessionType.R.equals(bestTotalTimeSessionRanking.getSession().getSessionType())) {
            grid.addColumn(SessionRankingRenderer.createTotalTimeRenderer(bestTotalTimeSessionRanking))
                    .setHeader("Total Time")
                    .setAutoWidth(true)
                    .setFlexGrow(0)
                    .setSortable(true)
                    .setComparator(SessionRanking::getTotalTimeMillis);
        }
        grid.addColumn(SessionRankingRenderer.createLapTimeRenderer(bestLapTimeSessionRanking))
                .setHeader("Fastest Lap")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(SessionRanking::getBestLapTimeMillis);

        grid.setItems(filteredSessionRankings);
        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setPartNameGenerator(new SessionRankingDNFNameGenerator(bestTotalTimeSessionRanking));

        return grid;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final RouteParameters routeParameters = event.getRouteParameters();

        String sessionIdParameter = routeParameters.get(ROUTE_PARAMETER_SESSION_ID).orElseThrow();

        try {
            Integer sessionId = Integer.valueOf(sessionIdParameter);

            if (!sessionService.sessionExists(sessionId)) {
                throw new IllegalArgumentException("Session with id " + sessionId + " does not exist.");
            }

            add(createSessionInformation(sessionId));
            addAndExpand(createLeaderboardGrid(sessionId));
            add(componentUtils.createFooter());
        } catch (IllegalArgumentException e) {
            event.rerouteToError(NotFoundException.class);
        }
    }
}
