package de.sustineo.simdesk.entities.json.kunos.acc.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccCupCategory {
    OVERALL(0, "OVERALL"),
    PRO_AM(1, "PRO-AM"),
    AM(2, "AM"),
    SILVER(3, "SILVER"),
    NATIONAL(4, "NATIONAL"),
    UNKNOWN(255, "UNKNOWN");

    @JsonValue
    private final int id;
    private final String name;

    public static AccCupCategory getById(int id) {
        for (AccCupCategory cupCategory : AccCupCategory.values()) {
            if (cupCategory.id == id) {
                return cupCategory;
            }
        }

        return UNKNOWN;
    }
}
