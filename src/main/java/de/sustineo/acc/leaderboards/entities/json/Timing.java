package de.sustineo.acc.leaderboards.entities.json;

import lombok.Data;

import java.util.List;

@Data
public class Timing {
    private Long lastLap;
    private List<Long> lastSplits;
    private Long bestLap;
    private List<Long> bestSplits;
    private Long totalTime;
    private Integer lapCount;
    private Long lastSplitId;
}
