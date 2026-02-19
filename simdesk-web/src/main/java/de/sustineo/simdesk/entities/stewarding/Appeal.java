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
public class Appeal {
    private Integer id;
    private Integer decisionId;
    private Integer filedByUserId;
    private Integer filedByEntryId;
    private String reason;
    private AppealStatus status;
    private String response;
    private Integer respondedByUserId;
    private Instant filedAt;
    private Instant respondedAt;
}
