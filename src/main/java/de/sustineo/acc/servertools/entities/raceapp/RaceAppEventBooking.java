package de.sustineo.acc.servertools.entities.raceapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RaceAppEventBooking {
    @JsonProperty("Drivers")
    List<RaceAppDriver> drivers;
    @JsonProperty("PerformanceClass")
    String performanceClass;
    @JsonProperty("Class")
    String carClass;
    @JsonProperty("Model")
    String carModel;
    @JsonProperty("CarNumber")
    String carNumber;
    @JsonProperty("CarName")
    String carName;
    @JsonProperty("TeamName")
    String teamName;
    @JsonProperty("Tag")
    String tag;
    @JsonProperty("EventBooking")
    EventBooking eventBooking;

    @Data
    private static class EventBooking {
        @JsonProperty("IsGuest")
        Boolean isGuest;
        @JsonProperty("IsConfirmed")
        Boolean isConfirmed;
        @JsonProperty("ChangedFromSeriesBooking")
        Boolean isDifferentFromSeriesBooking;
    }
}
