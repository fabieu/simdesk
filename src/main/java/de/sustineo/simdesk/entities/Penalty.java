package de.sustineo.simdesk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Penalty extends Entity {
    private static final int NO_LAP = 0;
    private static final String NO_PENALTY = "None";
    private static final String TIME_PENALTY = "TP";


    private static final Map<String, String> PENALTY_MAP = Map.of(
            NO_PENALTY, PLACEHOLDER,
            "DriveThrough", "DT",
            "Disqualified", "DSQ",
            "PostRaceTime", TIME_PENALTY,
            "StopAndGo_30", "SG30"
    );

    private static final Map<String, String> REASON_MAP = Map.of(
            "Cutting", "Cutting",
            "PitSpeeding", "Speeding in pits"
    );

    private String id;
    private Integer sessionId;
    private Integer carId;
    private String reason;
    private String penalty;
    private Integer penaltyValue;
    private Integer violationLap;
    private Integer clearedLap;
    private Boolean postRace;

    public boolean isValid() {
        return !NO_PENALTY.equals(penalty);
    }

    @JsonIgnore
    public Integer getViolationLapCorrected() {
        if (violationLap == null || violationLap == NO_LAP) {
            return null;
        } else {
            return violationLap;
        }
    }

    @JsonIgnore
    public Integer getClearedLapCorrected() {
        if (clearedLap == null || clearedLap == NO_LAP) {
            return null;
        } else {
            return clearedLap;
        }
    }

    @JsonIgnore
    public String getPenaltyAbbreviation() {
        String abbreviation = PENALTY_MAP.getOrDefault(penalty, UNKNOWN);

        if (TIME_PENALTY.equals(abbreviation) && penaltyValue != null) {
            return String.format("%s (%ss)", abbreviation, penaltyValue);
        }

        return abbreviation;
    }

    @JsonIgnore
    public String getReasonDescription() {
        return REASON_MAP.getOrDefault(reason, UNKNOWN);
    }
}
