package de.sustineo.acc.servertools.entities.json.kunos;

import lombok.Data;

import java.util.List;

@Data
public class AccTiming {
    private Long lastLap;
    private List<Long> lastSplits;
    private Long bestLap;
    private List<Long> bestSplits;
    private Long totalTime;
    private Integer lapCount;
    private Long lastSplitId;
}