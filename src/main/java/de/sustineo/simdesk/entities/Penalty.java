package de.sustineo.simdesk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "penalty")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
public class Penalty extends Model {
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "reason")
    private String reason;

    @Column(name = "penalty")
    private String penalty;

    @Column(name = "penalty_value")
    private Integer penaltyValue;

    @Column(name = "violation_lap")
    private Integer violationLap;

    @Column(name = "cleared_lap")
    private Integer clearedLap;

    @Column(name = "post_race")
    private Boolean postRace;

    @Column(name = "insert_datetime")
    @CreationTimestamp
    private Instant insertDatetime;

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
