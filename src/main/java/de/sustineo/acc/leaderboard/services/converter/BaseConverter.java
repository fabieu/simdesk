package de.sustineo.acc.leaderboard.services.converter;

import de.sustineo.acc.leaderboard.entities.enums.CarGroup;

public class BaseConverter {
    protected Long fixBadTiming(Long time) {
        if (time == null || time >= Integer.MAX_VALUE) {
            return 0L;
        }

        return time;
    }

    protected CarGroup fixBadCarGroup(CarGroup carGroup, Integer carModelId) {
        // Fix car group for the BMW M2 CS Racing
        if (carModelId == 27) {
            return CarGroup.TCX;
        }

        return carGroup;
    }
}
