package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import de.sustineo.acc.leaderboard.entities.enums.CupCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardLine {
    private Integer id;
    private Integer sessionId;
    private Integer ranking;
    private CupCategory cupCategory;
    private Integer carId;
    private CarGroup carGroup;
    private Integer carModelId;
    private Integer raceNumber;
    private List<Driver> drivers;
    private Long bestLapTimeMillis;
    private Long bestSplit1Millis;
    private Long bestSplit2Millis;
    private Long bestSplit3Millis;
    private Long totalTimeMillis;
    private Integer lapCount;
}
