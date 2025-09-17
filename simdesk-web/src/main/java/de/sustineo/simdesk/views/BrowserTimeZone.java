package de.sustineo.simdesk.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.server.VaadinSession;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public final class BrowserTimeZone {
    /**
     * Initializes the {@link ExtendedClientDetails} in the current session.
     */
    public static void init() {
        if (getExtendedClientDetails() == null) {
            UI.getCurrent().getPage().retrieveExtendedClientDetails(BrowserTimeZone::setExtendedClientDetails);
        }
    }

    /**
     * Returns the current {@link ExtendedClientDetails}, which is stored in the current session.
     * You need to populate this field first, by using {@link #init()},
     * otherwise this will return null.
     */
    private static ExtendedClientDetails getExtendedClientDetails() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            return null;
        }

        if (session.hasLock()) {
            return session.getAttribute(ExtendedClientDetails.class);
        }

        AtomicReference<ExtendedClientDetails> ref = new AtomicReference<>();
        session.accessSynchronously(() -> ref.set(session.getAttribute(ExtendedClientDetails.class)));
        return ref.get();
    }

    private static void setExtendedClientDetails(ExtendedClientDetails details) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            return;
        }

        if (session.hasLock()) {
            session.setAttribute(ExtendedClientDetails.class, details);
            return;
        }

        session.accessSynchronously(() -> session.setAttribute(ExtendedClientDetails.class, details));
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

    /**
     * Returns the current time zone of the browser as a string.
     *
     * @return String representation of the time zone.
     */
    public static String getDisplayName() {
        return get().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    /**
     * Converts the given Instant to a LocalDateTime in the browser's time zone.
     *
     * @param instant the Instant to convert
     * @return LocalDateTime in the browser's time zone, or null if the instant is null
     */
    public static LocalDateTime atLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }

        return LocalDateTime.ofInstant(instant, get());
    }
}