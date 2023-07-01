package de.sustineo.acc.leaderboard.views.renderers.ranking;

public class RankingTemplates {
    static final String RACE_NUMBER_TEMPLATE = "#${item.raceNumber}";
    static final String RACE_NUMBER_TEMPLATE_NUMBER = "raceNumber";
    static final String DRIVERS_TEMPLATE = """
            <vaadin-horizontal-layout theme="spacing" style="align-items: center;">
                ${item.drivers.map(driver => html`
                <vaadin-vertical-layout style="justify-content: center; align-items: center;">
                    <vaadin-button theme="tertiary">${driver.fullName}</vaadin-button>
                    <span>${driver.prettyDriveTime}</span>
                </vaadin-vertical-layout>
                `)}
            </vaadin-horizontal-layout>
            """;
    static final String DRIVERS_TEMPLATE_DRIVERS = "drivers";

    static final String TIMING_TEMPLATE = """
                <vaadin-vertical-layout style="align-items: end;">
                    <span>
                        ${item.time}
                     </span>
                    <span style="font-size: var(--lumo-font-size-s); color: var(${item.color});">
                        ${item.lapGap && item.lapGap > 0 ? item.lapGap + " lap(s) " : ""} ${item.timeGap}
                    </span>
                </vaadin-vertical-layout>
            """;
    static final String TIMING_TEMPLATE_TIME = "time";
    static final String TIMING_TEMPLATE_TIME_GAP = "timeGap";
    static final String TIMING_TEMPLATE_LAP_GAP = "lapGap";
    static final String TIMING_TEMPLATE_COLOR = "color";
}
