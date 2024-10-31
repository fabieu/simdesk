package de.sustineo.simdesk.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.server.VaadinSession;

import java.time.ZoneId;
import java.time.ZoneOffset;

public final class BrowserTimeZone {
    /**
     * Returns the current {@link ExtendedClientDetails}, which is stored in the current session.
     * You need to populate this field first, by using {@link #init()},
     * otherwise this will return null.
     */
    public static ExtendedClientDetails getExtendedClientDetails() {
        return VaadinSession.getCurrent().getAttribute(ExtendedClientDetails.class);
    }

    public static void setExtendedClientDetails(ExtendedClientDetails extendedClientDetails) {
        VaadinSession.getCurrent().setAttribute(ExtendedClientDetails.class, extendedClientDetails);
    }

    /**
     * Initializes the {@link ExtendedClientDetails} in the current session.
     */
    public static void init() {
        if (getExtendedClientDetails() == null) {
            UI.getCurrent().getPage().retrieveExtendedClientDetails(BrowserTimeZone::setExtendedClientDetails);
        }
    }

    /**
     * Returns the current time zone of the browser.
     *
     * @return ZoneId, either a zone ID or a zone offset.
     */
    public static ZoneId get() {
        final ExtendedClientDetails details = getExtendedClientDetails();
        if (details != null) {
            if (details.getTimeZoneId() != null && !details.getTimeZoneId().isBlank()) {
                // take into account zone ID. This is important for historical dates, to properly compute date with daylight savings.
                return ZoneId.of(details.getTimeZoneId());
            } else {
                // fallback to time zone offset
                return ZoneOffset.ofTotalSeconds(details.getTimezoneOffset() / 1000);
            }
        }

        return ZoneOffset.UTC;
    }
}