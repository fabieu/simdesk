package de.sustineo.simdesk.entities.json.kunos;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum AccCupCategory {
    OVERALL(0, "OVERALL"),
    PRO_AM(1, "PRO-AM"),
    AM(2, "AM"),
    SILVER(3, "SILVER"),
    NATIONAL(4, "NATIONAL");

    @JsonValue
    private final int id;
    private final String name;

    AccCupCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
