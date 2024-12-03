package de.sustineo.simdesk.entities;

import org.apache.commons.lang3.EnumUtils;

import java.util.Arrays;
import java.util.List;

public enum CarGroup {
    GT3, GT2, GTC, GT4, TCX, UNKNOWN;

    public static boolean exists(String carGroup) {
        return carGroup != null && EnumUtils.isValidEnumIgnoreCase(CarGroup.class, carGroup);
    }

    public static List<CarGroup> getValid() {
        return Arrays.stream(CarGroup.values())
                .filter(carGroup -> carGroup != UNKNOWN)
                .toList();
    }

    public static List<String> getValidNames() {
        return getValid().stream()
                .map(Enum::name)
                .toList();
    }
}
