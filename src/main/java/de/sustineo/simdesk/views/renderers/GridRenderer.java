package de.sustineo.simdesk.views.renderers;

public class GridRenderer {
    static final String RACE_NUMBER_TEMPLATE = """
                #${item.raceNumber}
                ${item.ballastKg ? html`<span theme="badge pill">${item.ballastKg}kg</span>` : ""}
            """;
    static final String RACE_NUMBER_TEMPLATE_NUMBER = "raceNumber";
    static final String RACE_NUMBER_TEMPLATE_BALLAST = "ballastKg";

    static final String DRIVERS_TEMPLATE = """
            <div style="display: flex; flex-wrap: wrap; gap: var(--lumo-space-s); align-items: center;">
                ${item.drivers.map(driver => html`
                    <div style="display: inline-block">
                        <span>${driver.fullNameCensored}</span>
                        <span theme="badge contrast pill" title="Valid laps/Invalid laps - Driving Time">
                            ${driver.validLapsCount || 0}/${driver.invalidLapsCount || 0}
                            ${driver.driveTimeMillis ? `- ${driver.prettyDriveTime}` : ""}
                        </span>
                    </div>
                `)}
            </div>
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

    static final String PENALTY_SERVED_TEMPLATE = """
            ${!item.postRace ? html`<vaadin-icon icon="lumo:checkmark" style="color: green;"></vaadin-icon>` : html`<vaadin-icon icon="lumo:cross" style="color: red;"></vaadin-icon>`}
            """;
    static final String PENALTY_SERVED_TEMPLATE_BOOLEAN = "postRace";

    static final String BOP_BALLAST_TEMPLATE = """
            ${item.ballastKg ? item.ballastKg + " kg" : ""}
            """;
    static final String BOP_BALLAST_TEMPLATE_BALLAST_KG = "ballastKg";

    static final String BOP_RESTRICTOR_TEMPLATE = """
            ${item.restrictor ? item.restrictor + " %" : ""}
            """;
    static final String BOP_RESTRICTOR_TEMPLATE_RESTRICTOR = "restrictor";

    public static String enrichNumber(Integer number) {
        if (number == null) {
            return "";
        } else if (number > 0) {
            return "+" + number;
        } else if (number < 0) {
            return "" + number;
        } else {
            return " " + number;
        }
    }

    public static String getTimeColor(Long gapMillis) {
        if (gapMillis < 0) {
            return "--lumo-success-text-color";
        } else if (gapMillis == 0) {
            return "--lumo-secondary-text-color";
        } else {
            return "--lumo-error-text-color";
        }
    }
}
