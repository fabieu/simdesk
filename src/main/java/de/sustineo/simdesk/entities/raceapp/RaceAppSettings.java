package de.sustineo.simdesk.entities.raceapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RaceAppSettings {
    @JsonProperty("CommunityName")
    private String communityName;
    @JsonProperty("Name")
    private String name;
}
