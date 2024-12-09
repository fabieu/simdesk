package de.sustineo.simdesk.entities.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpenWeatherPrecipitation {
    @JsonProperty("1h")
    private Double precipitation;
}
