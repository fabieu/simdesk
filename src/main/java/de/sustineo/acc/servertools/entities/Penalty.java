package de.sustineo.acc.servertools.entities;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Penalty {
    private String id;
    private Integer sessionId;
    private Integer carId;
    private String reason;
    private String penalty;
    private Integer penaltyValue;
    private Integer violationLap;
    private Integer clearedLap;
    private Boolean postRace;
}
