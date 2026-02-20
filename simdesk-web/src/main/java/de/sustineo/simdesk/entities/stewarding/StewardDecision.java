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
public class StewardDecision {
    private String id;
    private String incidentId;
    private String sessionId;
    private Integer decidedByUserId;
    private String penaltyDefinitionId;
    private String customPenalty;
    private String reasoning;
    private String reasoningTemplateId;
    private Boolean isNoAction;
    private String penalizedEntryId;
    private String penalizedCarText;
    private Instant decidedAt;
    private String supersededById;
    private Boolean isActive;
}
