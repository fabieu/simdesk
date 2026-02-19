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
    private Integer id;
    private Integer incidentId;
    private Integer sessionId;
    private Integer decidedByUserId;
    private Integer penaltyDefinitionId;
    private String customPenalty;
    private String reasoning;
    private Integer reasoningTemplateId;
    private Boolean isNoAction;
    private Integer penalizedEntryId;
    private String penalizedCarText;
    private Instant decidedAt;
    private Integer supersededById;
    private Boolean isActive;
}
