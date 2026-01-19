package de.sustineo.simdesk.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.component.page.Page;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

public final class BrowserTime {
    /**
     * Returns the current time zone of the browser.
     *
     * @return ZoneId, either a zone ID or a zone offset.
     */
    public static ZoneId getZoneId() {
        final ExtendedClientDetails details = Optional.ofNullable(UI.getCurrent())
                .map(UI::getPage)
                .map(Page::getExtendedClientDetails)
                .orElse(null);

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
        return getZoneId().getDisplayName(TextStyle.FULL, Locale.getDefault());
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

        return LocalDateTime.ofInstant(instant, getZoneId());
    }
}