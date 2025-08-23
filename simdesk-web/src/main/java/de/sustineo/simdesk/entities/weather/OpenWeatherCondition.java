package de.sustineo.simdesk.entities.weather;

import lombok.Data;

@Data
public class OpenWeatherCondition {
    private Integer id;
    private String main;
    private String description;
    private String icon;
}
