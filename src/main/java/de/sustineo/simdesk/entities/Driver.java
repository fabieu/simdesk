package de.sustineo.simdesk.entities;

import de.sustineo.simdesk.utils.FormatUtils;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
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
    private Long driveTimeMillis;
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

        return String.join(" ", firstName, lastName);
    }

    public String getEntireName() {
        String driverFullName = getFullName();

        if (shortName == null) {
            return driverFullName;
        } else {
            return driverFullName + " (" + shortName + ")";
        }
    }

    @SuppressWarnings("unused")
    public String getPrettyDriveTime() {
        return FormatUtils.formatDriveTime(driveTimeMillis);
    }
    
    public String toString() {
        return getFullName();
    }
}
