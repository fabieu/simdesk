package de.sustineo.simdesk.entities.weather;

import lombok.Data;

import java.util.List;

@Data
public class OpenWeatherAlert {
    private String senderName;
    private String event;
    private Long start;
    private Long end;
    private String description;
    private List<String> tags;
}
