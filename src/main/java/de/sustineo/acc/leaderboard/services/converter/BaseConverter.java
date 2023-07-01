package de.sustineo.acc.leaderboard.services.converter;

public class BaseConverter {
    protected Long filterBadTiming(Long time) {
        if (time == null || time >= Integer.MAX_VALUE) {
            return 0L;
        }

        return time;
    }
}
