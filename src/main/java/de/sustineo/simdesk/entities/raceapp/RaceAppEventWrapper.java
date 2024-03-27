package de.sustineo.simdesk.entities.raceapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RaceAppEventWrapper {
    @JsonProperty("Event")
    private RaceAppEvent event;
    @JsonProperty("Info")
    private String info;
    @JsonProperty("PerformanceClasses")
    private List<String> performanceClasses;
    @JsonProperty("DriverToPerformanceClass")
    private Map<String, String> performanceClassDriverMap;
    @JsonProperty("SeriesSettings")
    private RaceAppSettings seriesSettings;
}
