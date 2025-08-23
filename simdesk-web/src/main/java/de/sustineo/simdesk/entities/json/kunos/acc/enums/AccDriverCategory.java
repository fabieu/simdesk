package de.sustineo.simdesk.entities.json.kunos.acc.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccDriverCategory {
    BRONZE(0, "Bronze"),
    SILVER(1, "Silver"),
    GOLD(2, "Gold"),
    PLATINUM(3, "Platinum"),
    UNKNOWN(255, "Unknown");

    @JsonValue
    private final int id;
    private final String name;

    public static AccDriverCategory getById(int id) {
        for (AccDriverCategory driverCategory : AccDriverCategory.values()) {
            if (driverCategory.id == id) {
                return driverCategory;
            }
        }

        return UNKNOWN;
    }
}