package de.sustineo.acc.leaderboard.entities.enums;

import org.apache.commons.lang3.EnumUtils;

public enum CarGroup {
    GT3, GT4, TCX, Cup, ST, CHL;

    public static boolean isValid(String carGroup) {
        return carGroup != null && EnumUtils.isValidEnum(CarGroup.class, carGroup.toUpperCase());
    }

    public static CarGroup of(String carGroup) {
        return CarGroup.valueOf(carGroup.toUpperCase());
    }
}
