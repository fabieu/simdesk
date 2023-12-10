package de.sustineo.acc.servertools.entities.raceapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RaceAppDriver {
    @JsonProperty("Id")
    private Integer id;
    @JsonProperty("Name")
    private String name;

    public String toString() {
        return name;
    }
}
