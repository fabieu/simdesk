package de.sustineo.simdesk.entities.json.kunos;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum AccNationality {
    //TODO: Add all nationalities
    GERMANY(2, "Germany");

    @JsonValue
    private final int id;
    private final String name;

    AccNationality(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
