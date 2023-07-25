package de.sustineo.acc.servertools.utils;

import de.sustineo.acc.servertools.entities.Entity;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FormatUtils {
    private static final String DRIVE_TIME_FORMAT = "HH:mm:ss";
    private static final String TIMING_FORMAT = "mm:ss.SSS";
    private static final String TIMING_FORMAT_LONG = "HH:mm:ss.SSS";
    private static final String DATETIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    private static final DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern(DATETIME_FORMAT).withZone(ZoneId.systemDefault());

    public static String formatLapTime(Long millis) {
        if (millis == null) {
            return Entity.EMPTY;
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
            return Entity.EMPTY;
        }

        if (millis > Duration.ofHours(1).toMillis()) {
            return DurationFormatUtils.formatDuration(millis, TIMING_FORMAT_LONG, true);
        } else if (millis >= 0) {
            return DurationFormatUtils.formatDuration(millis, TIMING_FORMAT, true);
        } else {
            return Entity.EMPTY;
        }
    }

    public static String formatDriveTime(Long millis) {
        if (millis == null) {
            return Entity.EMPTY;
        }

        return DurationFormatUtils.formatDuration(millis, DRIVE_TIME_FORMAT, true);
    }

    public static String formatPercentage(Double value) {
        if (value == null) {
            return Entity.EMPTY;
        }

        return String.format("%.2f", value * 100) + "%";
    }
    public static String formatDatetime(Instant instant) {
        if (instant == null) {
            return Entity.EMPTY;
        }

        return dateTimeFormatter.format(instant);
    }
}
