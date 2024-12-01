package de.sustineo.simdesk.entities.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OpenWeatherHourlyForecast extends OpenWeather {
    @JsonProperty("temp")
    private Double temperature;
    @JsonProperty("feels_like")
    private Double feelsLike;
    @JsonProperty("pop")
    private Double probabilityOfPrecipitation;
    private OpenWeatherPrecipitation rain;
    private OpenWeatherPrecipitation snow;
}
