package de.sustineo.acc.leaderboard.views.renderers;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import de.sustineo.acc.leaderboard.entities.ranking.SessionRanking;

public class SessionRankingRenderer {
    private static final String RACE_NUMBER_TEMPLATE = "#${item.raceNumber}";
    private static final String RACE_NUMBER_TEMPLATE_NUMBER = "raceNumber";
    private static final String DRIVERS_TEMPLATE = """
            <vaadin-horizontal-layout>
                ${item.drivers.map(driver => html`
                <span>${driver.fullName}</span>
                `)}
            </vaadin-horizontal-layout>
            """;
    private static final String DRIVERS_TEMPLATE_DRIVERS = "drivers";

    public static Renderer<SessionRanking> createRaceNumberRenderer() {
        return LitRenderer.<SessionRanking>of(RACE_NUMBER_TEMPLATE)
                .withProperty(RACE_NUMBER_TEMPLATE_NUMBER, SessionRanking::getRaceNumber);
    }

    public static Renderer<SessionRanking> createDriversRenderer() {
        return LitRenderer.<SessionRanking>of(DRIVERS_TEMPLATE)
                .withProperty(DRIVERS_TEMPLATE_DRIVERS, SessionRanking::getDrivers);
    }
}
