package de.sustineo.acc.servertools.entities.enums;

import org.apache.commons.lang3.EnumUtils;

public enum CarGroup {
    GT3, GT4, TCX, CUP, ST, CHL, UNKNOWN;

    public static boolean isValid(String carGroup) {
        return carGroup != null && EnumUtils.isValidEnumIgnoreCase(CarGroup.class, carGroup);
    }
}