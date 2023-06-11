package de.sustineo.acc.leaderboard.utils;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FormatUtils {
    private final static String LAP_TIME_FORMAT = "mm:ss.SSS";
    private final static String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";

    public static String formatLapTime(Long millis) {
        return DurationFormatUtils.formatDuration(millis, LAP_TIME_FORMAT, true);
    }

    public static String formatPercentage(Double value) {
        return String.format("%.2f", value * 100) + "%";
    }
    public static String formatDatetime(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
                .withZone(ZoneId.systemDefault());;
        return formatter.format(instant);
    }
}
