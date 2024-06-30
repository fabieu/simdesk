package de.sustineo.simdesk.entities;

import lombok.Data;

import java.time.Instant;

@Data
public class DynamicProperty {
    private Integer id;
    private String key;
    private String value;
    private String description;
    private boolean active;
    private Instant updateDatetime;
}
