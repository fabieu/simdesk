package de.sustineo.acc.leaderboard.views.renderers.ranking;

public class RankingTemplates {
    static final String RACE_NUMBER_TEMPLATE = "#${item.raceNumber}";
    static final String RACE_NUMBER_TEMPLATE_NUMBER = "raceNumber";
    static final String DRIVERS_TEMPLATE = """
            <vaadin-vertical-layout>
                ${item.drivers.map(driver => html`
                <span>${driver.fullName} - ${driver.prettyDriveTime}</span>
                `)}
            </vaadin-vertical-layout>
            """;
    static final String DRIVERS_TEMPLATE_DRIVERS = "drivers";

    static final String TIMING_TEMPLATE = """
                <vaadin-vertical-layout style="align-items: end;">
                    <span>
                        ${item.time}
                     </span>
                    <span style="font-size: var(--lumo-font-size-s); color: var(${item.color});">
                        ${item.gap}
                    </span>
                </vaadin-vertical-layout>
            """;
    static final String TIMING_TEMPLATE_TIME = "time";
    static final String TIMING_TEMPLATE_GAP = "gap";
    static final String TIMING_TEMPLATE_COLOR = "color";
}
