package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sustineo.simdesk.entities.SessionType;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@Data
@ToString
public final class AccSession {
    private String sessionType;
    private String trackName;
    private Integer sessionIndex;
    private Integer raceWeekendIndex;
    private String metaData;
    private String serverName;
    private AccSessionResult sessionResult;
    private List<AccLap> laps;
    private List<AccPenalty> penalties;
    @JsonProperty("post_race_penalties")
    private List<AccPenalty> postRacePenalties;

    public SessionType getSessionType(){
        if (sessionType == null) {
            return SessionType.UNKNOWN;
        }

        if (sessionType.startsWith("FP")){
            return SessionType.FP;
        } else if (sessionType.startsWith("Q")){
            return SessionType.Q;
        } else if (sessionType.startsWith("R")){
            return SessionType.R;
        } else {
            return SessionType.UNKNOWN;
        }
    }

    public Optional<AccTeam> getTeamById(Integer teamId) {
        return sessionResult.getLeaderboardLines().stream()
                .filter(accLeaderboardLine -> accLeaderboardLine.getTeam().getTeamId().equals(teamId))
                .findFirst()
                .map(AccLeaderboardLine::getTeam);

    }
}
