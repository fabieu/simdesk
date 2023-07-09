package de.sustineo.acc.leaderboard.views.renderers.ranking;

public class RankingTemplates {
    static final String RACE_NUMBER_TEMPLATE = "#${item.raceNumber}";
    static final String RACE_NUMBER_TEMPLATE_NUMBER = "raceNumber";
    static final String DRIVERS_TEMPLATE = """
            <vaadin-horizontal-layout theme="spacing" style="align-items: center;">
                ${item.drivers.map(driver => html`
                <vaadin-horizontal-layout">
                    <div>
                        <vaadin-button theme="tertiary">${driver.fullName}</vaadin-button>
                        ${driver.prettyDriveTime ? html`<span theme="badge contrast pill" title="Driving time">${driver.prettyDriveTime}</span>` : ""}
                    </div>
                </vaadin-horizontal-layout>
                `)}
            </vaadin-horizontal-layout>
            """;
    static final String DRIVERS_TEMPLATE_DRIVERS = "drivers";

    static final String TIMING_TEMPLATE = """
                <vaadin-vertical-layout style="align-items: end;">
                    <div>
                        ${item.fastestLap ? html`<span theme="badge success pill small" title="Fastest lap">FL</span>` : ""}
                        <span>${item.time}</span>
                     </div>
                    <span style="font-size: var(--lumo-font-size-s); color: var(${item.color});">
                        ${item.lapCount && item.lapCount > 0 && (item.bestLapCount - item.lapCount) > 0 ? (item.bestLapCount - item.lapCount) + " lap(s) " : ""} ${item.timeGap}
                    </span>
                </vaadin-vertical-layout>
            """;
    static final String TIMING_TEMPLATE_TIME = "time";
    static final String TIMING_TEMPLATE_TIME_GAP = "timeGap";
    static final String TIMING_TEMPLATE_BEST_LAP_COUNT = "bestLapCount";
    static final String TIMING_TEMPLATE_LAP_COUNT = "lapCount";
    static final String TIMING_TEMPLATE_COLOR = "color";
    static final String TIMING_TEMPLATE_FASTEST_LAP = "fastestLap";
}
