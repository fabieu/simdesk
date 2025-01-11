package de.sustineo.simdesk.utils;

import de.sustineo.simdesk.entities.Model;
import de.sustineo.simdesk.views.BrowserTimeZone;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class FormatUtils {
    private static final String TIMING_FORMAT = "mm:ss.SSS";
    private static final String TIMING_FORMAT_LONG = "HH:mm:ss.SSS";
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATETIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    private static final String DATETIME_FORMAT_SAFE = "yyyyMMdd_HHmmss";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
    private static final DateTimeFormatter dateTimeFormatterSafe = DateTimeFormatter.ofPattern(DATETIME_FORMAT_SAFE);

    public static String formatLapTime(Long millis) {
        if (millis == null) {
            return Model.EMPTY;
        }

        if (millis > Duration.ofHours(1).toMillis()) {
            return DurationFormatUtils.formatDuration(millis, TIMING_FORMAT_LONG, true);
        } else if (millis >= 0) {
            return DurationFormatUtils.formatDuration(millis, TIMING_FORMAT, true);
        } else {
            return "-" + DurationFormatUtils.formatDuration(millis * -1, TIMING_FORMAT, true);
        }
    }

    public static String formatTotalTime(Long millis) {
        if (millis == null) {
            return Model.EMPTY;
        }

        if (millis > Duration.ofHours(1).toMillis()) {
            return DurationFormatUtils.formatDuration(millis, TIMING_FORMAT_LONG, true);
        } else if (millis >= 0) {
            return DurationFormatUtils.formatDuration(millis, TIMING_FORMAT, true);
        } else {
            return Model.EMPTY;
        }
    }

    public static String formatDriveTime(Long millis) {
        if (millis == null) {
            return Model.EMPTY;
        }

        return DurationFormatUtils.formatDuration(millis, TIME_FORMAT, true);
    }

    public static String formatDatetime(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return Model.EMPTY;
        }

        return dateTimeFormatter.withZone(BrowserTimeZone.get()).format(temporalAccessor);
    }

    public static String formatDatetimeSafe(TemporalAccessor intemporalAccessor) {
        if (intemporalAccessor == null) {
            return Model.EMPTY;
        }

        return dateTimeFormatterSafe.withZone(BrowserTimeZone.get()).format(intemporalAccessor);
    }

    public static String formatDate(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return Model.EMPTY;
        }

        return dateFormatter.withZone(BrowserTimeZone.get()).format(temporalAccessor);
    }
}
