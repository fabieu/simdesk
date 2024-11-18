package de.sustineo.simdesk.entities.stewarding;

import lombok.Data;

import java.time.Instant;

@Data
public class StewardingEvent {
    private Integer id;
    private String simulationId;
    private String name;
    private String trackId;
    private Instant startDatetime;
    private Instant endDatetime;
    private Instant archiveDatetime;
    private Instant updateDatetime;
    private Instant insertDatetime;
}
