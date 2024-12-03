package de.sustineo.simdesk.entities.weather;

import lombok.Data;

import java.util.List;

@Data
public class OpenWeatherModel {
    private Double lat;
    private Double lon;
    private String timezone;
    private Integer timezoneOffset;
    private OpenWeatherCurrent current;
    private List<OpenWeatherMinutelyForecast> minutely;
    private List<OpenWeatherHourlyForecast> hourly;
    private List<OpenWeatherDailyForecast> daily;
    private List<OpenWeatherAlert> alerts;
}

