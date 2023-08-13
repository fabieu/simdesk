package de.sustineo.acc.servertools.entities.raceapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RaceAppSeriesDriver {
    @JsonProperty("Id")
    private Integer id;
    @JsonProperty("Name")
    private String name;
}
