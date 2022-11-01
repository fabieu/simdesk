package de.sustineo.acc.leaderboard.entities.json;

import lombok.Data;

import java.util.List;

@Data
public class LeaderboardLine {
    private Car car;
    private Driver currentDriver;
    private Integer currentDriverIndex;
    private Timing timing;
    private Integer missingMandatoryPitstop;
    private List<?> driverTotalTimes;
}
