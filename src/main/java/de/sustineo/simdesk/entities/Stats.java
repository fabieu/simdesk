package de.sustineo.simdesk.entities;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Stats extends Entity {
    private String totalSessions = UNKNOWN;
    private String totalDrivers = UNKNOWN;
    private String totalLaps = UNKNOWN;

    public static StatsBuilder builder() {
        return new Stats().toBuilder();
    }
}

