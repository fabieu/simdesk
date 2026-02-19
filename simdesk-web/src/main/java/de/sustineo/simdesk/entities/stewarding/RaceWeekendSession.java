package de.sustineo.simdesk.entities.stewarding;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RaceWeekendSession {
    private Integer id;
    private Integer raceWeekendId;
    private StewSessionType sessionType;
    private String title;
    private Instant startTime;
    private Instant endTime;
    private Integer sortOrder;
    private Instant createdAt;
}
