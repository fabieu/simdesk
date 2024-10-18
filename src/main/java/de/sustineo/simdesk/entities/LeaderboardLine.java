package de.sustineo.simdesk.entities;

import de.sustineo.simdesk.entities.json.kunos.AccCupCategory;
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
    private AccCupCategory cupCategory;
    private Integer carId;
    @Deprecated
    private CarGroup carGroup;
    private Integer carModelId;
    private Integer ballastKg;
    private Integer raceNumber;
    private List<Driver> drivers;
    private Long bestLapTimeMillis;
    private Long bestSplit1Millis;
    private Long bestSplit2Millis;
    private Long bestSplit3Millis;
    private Long totalTimeMillis;
    private Integer lapCount;
}
