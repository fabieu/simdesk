package de.sustineo.simdesk.entities;

import org.apache.commons.lang3.EnumUtils;

import java.util.EnumSet;
import java.util.Set;

public enum CarGroup {
    GT3,
    GT2,
    GTC,
    GT4,
    TCX,
    UNKNOWN;

    public static boolean exists(String carGroup) {
        return carGroup != null && EnumUtils.isValidEnumIgnoreCase(CarGroup.class, carGroup);
    }

    public static Set<CarGroup> getValid() {
        EnumSet<CarGroup> set = EnumSet.allOf(CarGroup.class);
        set.remove(UNKNOWN);
        return set;
    }
}
