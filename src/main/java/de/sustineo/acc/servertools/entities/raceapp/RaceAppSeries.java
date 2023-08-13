package de.sustineo.acc.servertools.entities.raceapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RaceAppSeries {
    @JsonProperty("Id")
    private Integer id;
    @JsonProperty("Info")
    private String info;
    @JsonProperty("Settings")
    private RaceAppSettings settings;
    @JsonProperty("Rules")
    private String rules;
    @JsonProperty("Results")
    private List<RaceAppResult> results;
    @JsonProperty("TeamTotals")
    private List<RaceAppTeamResult> teamResults;
}
