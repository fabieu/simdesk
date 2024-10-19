package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum AccDriverCategory {
    BRONZE(0, "Bronze"),
    SILVER(1, "Silver"),
    GOLD(2, "Gold"),
    PLATINUM(3, "Platinum");

    @JsonValue
    private final int id;
    private final String name;

    AccDriverCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }
}