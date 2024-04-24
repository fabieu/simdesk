package de.sustineo.simdesk.entities.enums;

import org.apache.commons.lang3.EnumUtils;

import java.util.Arrays;
import java.util.List;

public enum CarGroup {
    GT3, GT2, GT4, GTC, TCX, UNKNOWN;

    public static boolean isValid(String carGroup) {
        return carGroup != null && EnumUtils.isValidEnumIgnoreCase(CarGroup.class, carGroup);
    }

    public static List<String> getValidNames() {
        return Arrays.stream(CarGroup.values())
                .filter(carGroup -> carGroup != UNKNOWN)
                .map(Enum::name)
                .toList();
    }
}
