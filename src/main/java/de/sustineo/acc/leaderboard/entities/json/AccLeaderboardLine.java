package de.sustineo.acc.leaderboard.entities.json;

import lombok.Data;

import java.util.List;

@Data
public class AccLeaderboardLine {
    private AccCar car;
    private AccDriver currentDriver;
    private Integer currentDriverIndex;
    private AccTiming timing;
    private Integer missingMandatoryPitstop;
    private List<Long> driverTotalTimes;
}
