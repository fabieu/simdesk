package de.sustineo.acc.leaderboard.entities.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SessionResult {
    private Long bestLap;
    private List<Long> bestSplits;
    private boolean isWetSession;
    private Integer type;
    @JsonProperty("leaderBoardLines")
    private List<LeaderboardLine> leaderboardLines;
}
