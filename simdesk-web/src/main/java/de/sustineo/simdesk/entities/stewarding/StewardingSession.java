package de.sustineo.simdesk.entities.stewarding;

import de.sustineo.simdesk.entities.RaceTrack;
import lombok.Data;

import java.time.Instant;

@Data
public class StewardingSession {
    private Integer id;
    private String title;
    private String description;
    private Instant startDatetime;
    private Instant endDatetime;

    // Associations
    private RaceTrack raceTrack;
    private StewardingPenalties penalties;
    private StewardingEntrylist entrylist;
    private StewardReasonCodes reasonCodes;
}
