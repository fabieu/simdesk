package de.sustineo.simdesk.entities.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public class OpenWeatherCurrent extends OpenWeather {
    private Instant sunrise;
    private Instant sunset;
    @JsonProperty("temp")
    private Double temperature;
    @JsonProperty("feels_like")
    private Double feelsLike;
    private OpenWeatherPrecipitation rain;
    private OpenWeatherPrecipitation snow;
}
