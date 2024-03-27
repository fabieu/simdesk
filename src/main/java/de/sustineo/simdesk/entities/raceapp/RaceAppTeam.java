package de.sustineo.simdesk.entities.raceapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RaceAppTeam {
    @JsonProperty("Id")
    private Integer id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("PerformanceClass")
    private String performanceClass;
    @JsonProperty("VehicleClass")
    private String vehicleClass;
    @JsonProperty("VehicleModel")
    private String vehicleModel;
}
