package de.sustineo.acc.servertools.services.converter;

public class BaseConverter {
    protected Long fixBadTiming(Long time) {
        if (time == null || time >= Integer.MAX_VALUE) {
            return 0L;
        }

        return time;
    }
}