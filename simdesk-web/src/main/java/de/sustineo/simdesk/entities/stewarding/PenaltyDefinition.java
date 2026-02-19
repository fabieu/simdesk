package de.sustineo.simdesk.entities.stewarding;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PenaltyDefinition {
    private Integer id;
    private Integer catalogId;
    private String code;
    private String name;
    private String description;
    private String category;
    private PenaltySessionType sessionType;
    private String defaultPenalty;
    private Integer severity;
    private Integer sortOrder;
}
