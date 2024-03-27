package de.sustineo.simdesk.entities.raceapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RaceAppTeamResult {
    @JsonProperty("Team")
    private RaceAppTeam team;
    @JsonProperty("Points")
    private Double points;
    @JsonProperty("Position")
    private Integer position;
}
