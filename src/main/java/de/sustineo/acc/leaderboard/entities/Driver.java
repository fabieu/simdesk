package de.sustineo.acc.leaderboard.entities;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Driver extends Entity {
    private String playerId;
    private String firstName;
    private String lastName;
    private String shortName;
    private boolean locked;
    private Integer validLapsCount;
    private Integer invalidLapsCount;
    private Instant lastActivity;

    public Double getValidLapsPercentage() {
        if (validLapsCount == null || getTotalLapsCount() == null || getTotalLapsCount() <= 0) {
            return 0.0;
        }

        return (double) validLapsCount / (double) getTotalLapsCount();
    }

    public Integer getTotalLapsCount() {
        if (validLapsCount == null || invalidLapsCount == null) {
            return 0;
        }

        return validLapsCount + invalidLapsCount;
    }

    public String getFullName() {
        if (firstName == null || lastName == null) {
            return UNKNOWN;
        }

        String driverFullName = String.join(" ", firstName, lastName);

        if (shortName == null) {
            return driverFullName;
        } else {
            return driverFullName + " (" + shortName + ")";
        }
    }
}