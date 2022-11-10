package de.sustineo.acc.leaderboard.entities.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sustineo.acc.leaderboard.entities.enums.SessionType;
import de.sustineo.acc.leaderboard.entities.enums.Track;
import lombok.Data;

import java.util.List;

@Data
public class AccSession {
    private SessionType sessionType;
    private Track trackName;
    private Integer sessionIndex;
    private Integer raceWeekendIndex;
    private String metaData;
    private String serverName;
    private AccSessionResult sessionResult;
    private List<AccLap> laps;
    private List<AccPenalty> penalties;
    @JsonProperty("post_race_penalties")
    private List<AccPenalty> postRacePenalties;
}
