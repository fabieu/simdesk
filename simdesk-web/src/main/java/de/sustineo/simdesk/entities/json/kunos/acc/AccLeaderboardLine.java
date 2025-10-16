package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public final class AccLeaderboardLine {
    @JsonProperty("car")
    private AccTeam team;
    private AccDriver currentDriver;
    private Integer currentDriverIndex;
    private AccTiming timing;
    private Integer missingMandatoryPitstop;
    private List<Long> driverTotalTimes;
}
