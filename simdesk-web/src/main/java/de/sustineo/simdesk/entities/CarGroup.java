package de.sustineo.simdesk.entities;

import de.sustineo.simdesk.views.filter.GridEnum;
import org.apache.commons.lang3.EnumUtils;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum CarGroup implements GridEnum {
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

    public static Set<String> getValidNames() {
        return getValid().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public String getLabel() {
        return name();
    }
}
