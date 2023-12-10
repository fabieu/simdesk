package de.sustineo.acc.servertools.entities.raceapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class RaceAppEvent {
    @JsonProperty("Info")
    private String info;
    @JsonProperty("Start")
    private Instant startTime;
    @JsonProperty("End")
    private Instant endTime;
    @JsonProperty("EventBookings")
    private List<RaceAppEventBooking> eventBookings;
}
