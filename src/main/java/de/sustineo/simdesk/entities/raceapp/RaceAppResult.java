package de.sustineo.simdesk.entities.raceapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RaceAppResult {
    @JsonProperty("Id")
    private Integer id;
    @JsonProperty("Position")
    private Integer position;
    @JsonProperty("PositionInClass")
    private Integer positionInClass;
    @JsonProperty("TotalPoints")
    private Double totalPoints;
    @JsonProperty("PenaltyPoints")
    private Double PenaltyPoints;
    @JsonProperty("VehicleModel")
    private String VehicleModel;
    @JsonProperty("VehicleClass")
    private String VehicleClass;
    @JsonProperty("PerformanceClass")
    private String PerformanceClass;
    @JsonProperty("CarName")
    private String carName;
    @JsonProperty("SeriesDrivers")
    private List<RaceAppDriver> drivers;
    @JsonProperty("Tag")
    private String tag;

    public String getDriversString() {
        if (drivers == null || drivers.isEmpty()) {
            return "";
        }

        return drivers.stream()
                .map(RaceAppDriver::getName)
                .reduce((s, s2) -> s + ", " + s2)
                .orElse("");
    }
}
