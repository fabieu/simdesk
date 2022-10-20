package de.sustineo.acc.leaderboards.entities.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Result {
    private SessionType sessionType;
    private Track trackName;
    private Integer sessionIndex;
    private Integer raceWeekendIndex;
    private String metaData;
    private String serverName;
    private SessionResult sessionResult;
    private List<Lap> laps;
    private List<Penalty> penalties;
    @JsonProperty("post_race_penalties")
    private List<Penalty> postRacePenalties;
}
