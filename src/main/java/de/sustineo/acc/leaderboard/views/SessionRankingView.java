package de.sustineo.acc.leaderboard.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.sustineo.acc.leaderboard.configuration.VaadinConfiguration;
import de.sustineo.acc.leaderboard.entities.comparator.SessionRankingLapTimeComparator;
import de.sustineo.acc.leaderboard.entities.enums.SessionType;
import de.sustineo.acc.leaderboard.entities.ranking.SessionRanking;
import de.sustineo.acc.leaderboard.layouts.MainLayout;
import de.sustineo.acc.leaderboard.services.RankingService;
import de.sustineo.acc.leaderboard.services.SessionService;
import de.sustineo.acc.leaderboard.views.generators.SessionRankingPartNameGenerator;
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

    public SessionRankingView(RankingService rankingService, SessionService sessionService) {
        this.rankingService = rankingService;
        this.sessionService = sessionService;
        addClassName("sessions-view");
        setSizeFull();
    }


    private Component createSessionInformation(Integer sessionId) {
        return new Div();
    }

    private Component createLeaderboardGrid(Integer sessionId) {
        Grid<SessionRanking> grid = new Grid<>(SessionRanking.class, false);

        List<SessionRanking> sessionRankings = rankingService.getSessionRanking(sessionId);
        SessionRanking bestTotalTimeSessionRanking = sessionRankings.stream().findFirst().orElse(new SessionRanking());
        SessionRanking bestLapTimeSessionRanking = sessionRankings.stream().min(new SessionRankingLapTimeComparator()).orElse(new SessionRanking());
        grid.setItems(sessionRankings);

        grid.addColumn(SessionRanking::getRanking)
                .setHeader("#")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER);
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

        grid.setHeightFull();
        grid.setMultiSort(true, true);
        grid.setColumnReorderingAllowed(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setPartNameGenerator(new SessionRankingPartNameGenerator(bestTotalTimeSessionRanking));

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
            add(ComponentUtils.createFooter());
        } catch (IllegalArgumentException e) {
            event.rerouteToError(NotFoundException.class);
        }
    }
}
