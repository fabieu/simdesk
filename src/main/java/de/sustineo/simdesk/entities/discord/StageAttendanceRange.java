package de.sustineo.simdesk.entities.discord;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class StageAttendanceRange {
    private Instant joinTimestamp;
    private Instant leaveTimestamp;
}
