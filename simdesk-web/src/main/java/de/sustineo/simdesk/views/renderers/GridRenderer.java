package de.sustineo.simdesk.views.renderers;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.views.BaseView;
import de.sustineo.simdesk.views.LeaderboardDriverView;
import de.sustineo.simdesk.views.LeaderboardSessionDetailsView;

public class GridRenderer {
    static final String RACE_NUMBER_TEMPLATE = """
                #${item.raceNumber}
                ${item.ballastKg ? html`<span theme="badge pill">${item.ballastKg}kg</span>` : ""}
            """;
    static final String RACE_NUMBER_TEMPLATE_NUMBER = "raceNumber";
    static final String RACE_NUMBER_TEMPLATE_BALLAST = "ballastKg";

    static final String SESSION_REFERENCE_TEMPLATE = """
            <vaadin-button
                title="Show session"
                @click="${clickHandler}"
                theme="tertiary-inline small link">
                ${item.session.description}
            </vaadin-button>
            """;
    static final String SESSION_REFERENCE_TEMPLATE_SESSION = "session";
    static final String SESSION_REFERENCE_TEMPLATE_CLICK_HANDLER = "clickHandler";

    static final String DRIVER_REFERENCE_TEMPLATE = """
            <vaadin-button
                title="Show profile"
                @click="${clickHandler}"
                theme="tertiary-inline small link">
                ${item.driver.fullName}
            </vaadin-button>
            """;
    static final String DRIVER_REFERENCE_TEMPLATE_DRIVER = "driver";
    static final String DRIVER_REFERENCE_TEMPLATE_CLICK_HANDLER = "clickHandler";

    static final String DRIVERS_TEMPLATE = """
            <div style="display: flex; flex-wrap: wrap; gap: var(--lumo-space-s); align-items: center;">
                ${item.drivers.map(driver => html`
                    <div style="display: inline-block">
                        <vaadin-button
                            title="Show profile"
                            @click="${() => clickHandler(driver.id)}"
                            theme="tertiary-inline small link">
                            ${driver.fullName}
                        </vaadin-button>
                        <span theme="badge contrast pill" title="Valid laps/Invalid laps - Driving Time">
                            ${driver.validLapsCount || 0}/${driver.invalidLapsCount || 0}
                            ${driver.driveTimeMillis ? `- ${driver.prettyDriveTime}` : ""}
                        </span>
                    </div>
                `)}
            </div>
            """;
    static final String DRIVERS_TEMPLATE_DRIVERS = "drivers";
    static final String DRIVERS_TEMPLATE_CLICK_HANDLER = "clickHandler";

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

    protected static String enrichNumber(Integer number) {
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

    protected static String getLapTimeColor(Long gapMillis) {
        if (gapMillis < 0) {
            return "--lumo-success-text-color";
        } else if (gapMillis == 0) {
            return "--lumo-secondary-text-color";
        } else {
            return "--lumo-error-text-color";
        }
    }

    protected static String getTheoreticalLapTimeColor(Long gapMillis) {
        if (gapMillis == 0) {
            return "--lumo-success-text-color";
        } else {
            return "--lumo-error-text-color";
        }
    }

    protected static void redirectToDriverProfile(Driver driver) {
        if (driver == null || driver.getId() == null) {
            return;
        }

        RouterLink link = new RouterLink(LeaderboardDriverView.class, new RouteParameters(
                new RouteParam(BaseView.ROUTE_PARAMETER_DRIVER_ID, driver.getId())
        ));

        UI.getCurrent().getPage().open(link.getHref());
    }

    protected static void redirectToSessionDetails(Session session) {
        if (session == null || session.getFileChecksum() == null) {
            return;
        }

        RouterLink link = new RouterLink(LeaderboardSessionDetailsView.class, new RouteParameters(
                new RouteParam(BaseView.ROUTE_PARAMETER_FILE_CHECKSUM, session.getFileChecksum())
        ));

        UI.getCurrent().getPage().open(link.getHref());
    }
}
