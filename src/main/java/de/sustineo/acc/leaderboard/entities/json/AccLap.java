package de.sustineo.acc.leaderboard.entities.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AccLap {
    private Integer carId;
    private Integer driverIndex;
    @JsonProperty("laptime")
    private Long lapTimeMillis;
    @JsonProperty("isValidForBest")
    private Boolean valid;
    private List<Long> splits;
}
