package de.sustineo.simdesk.entities.json.kunos.acc;

import lombok.Data;

@Data
public final class AccPenalty {
    private Integer carId;
    private Integer driverIndex;
    private String reason;
    private String penalty;
    private Integer penaltyValue;
    private Integer violationInLap;
    private Integer clearedInLap;
}
