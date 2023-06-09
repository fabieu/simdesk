package de.sustineo.acc.leaderboard.utils;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class FormatUtils {
    private final static String LAP_TIME_FORMAT = "mm:ss.SSS";

    public static String formatLapTime(Long millis) {
        return DurationFormatUtils.formatDuration(millis, LAP_TIME_FORMAT, true);
    }
}
