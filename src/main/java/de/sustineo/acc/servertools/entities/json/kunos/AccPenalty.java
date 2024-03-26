package de.sustineo.acc.servertools.entities.json.kunos;

import lombok.Data;

@Data
public class AccPenalty {
    private Integer carId;
    private Integer driverIndex;
    private String reason;
    private String penalty;
    private Integer penaltyValue;
    private Integer violationInLap;
    private Integer clearedInLap;
}
