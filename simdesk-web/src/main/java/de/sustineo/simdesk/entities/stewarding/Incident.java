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
public class Incident {
    private Integer id;
    private Integer sessionId;
    private String title;
    private String description;
    private Integer lap;
    private String timestampInSession;
    private Double mapMarkerX;
    private Double mapMarkerY;
    private String videoUrl;
    private String involvedCarsText;
    private IncidentStatus status;
    private Integer reportedByUserId;
    private Instant createdAt;
    private Instant updatedAt;
}
