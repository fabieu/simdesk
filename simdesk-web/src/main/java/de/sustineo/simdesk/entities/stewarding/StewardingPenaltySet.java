package de.sustineo.simdesk.entities.stewarding;

import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class StewardingPenaltySet {
    private Integer id;
    private String title;
    private String description;
    private Set<StewardingPenalty> penalties;
    private Instant updateDatetime;
}
