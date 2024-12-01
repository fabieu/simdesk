package de.sustineo.simdesk.entities.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class OpenWeather {
    @JsonProperty("dt")
    private Instant timestamp;
    private Double temp;
    private Integer pressure;
    private Integer humidity;
    @JsonProperty("dew_point")
    private Double dewPoint;
    private Double clouds;
    @JsonProperty("uvi")
    private Double uvIndex;
    private Integer visibility;
    @JsonProperty("wind_speed")
    private Double windSpeed;
    @JsonProperty("wind_gust")
    private Double windGust;
    @JsonProperty("wind_deg")
    private Integer windDeg;
    private List<OpenWeatherCondition> weather;
}
