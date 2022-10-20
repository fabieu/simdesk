package de.sustineo.acc.leaderboards.entities.json;

import lombok.Data;

import java.util.List;

@Data
public class LeadboardLine {
    private Car car;
    private Driver currentDriver;
    private Integer currentDriverIndex;
    private Timing timing;
    private Integer missingMandatoryPitstop;
    private List<?> driverTotalTimes;
}
