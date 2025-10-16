package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public final class AccSessionResult {
    @JsonProperty("bestlap")
    private Long bestLap;
    private List<Long> bestSplits;
    private Boolean isWetSession;
    private Integer type;
    @JsonProperty("leaderBoardLines")
    private List<AccLeaderboardLine> leaderboardLines;
}
