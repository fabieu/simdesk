package de.sustineo.simdesk.entities.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class OpenWeatherMinutelyForecast {
    @JsonProperty("dt")
    private Instant timestamp;
    private Double precipitation;
}
