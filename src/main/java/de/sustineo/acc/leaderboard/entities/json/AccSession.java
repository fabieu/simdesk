package de.sustineo.acc.leaderboard.entities.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sustineo.acc.leaderboard.entities.enums.SessionType;
import de.sustineo.acc.leaderboard.entities.enums.Track;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@Data
@ToString
public class AccSession {
    private String sessionType;
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

    public Optional<AccCar> getCarById(Integer carId) {
        return sessionResult.getLeaderboardLines().stream()
                .filter(accLeaderboardLine -> accLeaderboardLine.getCar().getCarId().equals(carId))
                .findFirst()
                .map(AccLeaderboardLine::getCar);

    }
}
