package de.sustineo.simdesk.entities.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public class OpenWeatherDailyForecast extends OpenWeather {
    private Instant sunrise;
    private Instant sunset;
    private Instant moonrise;
    private Instant moonset;
    @JsonProperty("moon_phase")
    private Double moonPhase;
    private String summary;
    @JsonProperty("temp")
    private Temperature temperature;
    @JsonProperty("feels_like")
    private FeelsLike feelsLike;
    @JsonProperty("pop")
    private Double probabilityOfPrecipitation;

    @Data
    public static class Temperature {
        @JsonProperty("morn")
        private Double morning;
        @JsonProperty("day")
        private Double noon;
        @JsonProperty("eve")
        private Double evening;
        private Double night;
        private Double min;
        private Double max;
    }

    @Data
    public static class FeelsLike {
        @JsonProperty("morn")
        private Double morning;
        @JsonProperty("day")
        private Double noon;
        @JsonProperty("eve")
        private Double evening;
        private Double night;
    }
}
