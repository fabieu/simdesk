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
    private static final String UNKNOWN_DRIVER = "Unknown Driver";
    private static final String HIDDEN_FIRST_NAME = "Hidden";
    private static final String HIDDEN_LAST_NAME = "Driver";
    private static final String HIDDEN_SHORT_NAME = "HDR";
    private static final String HIDDEN_FULL_NAME = HIDDEN_FIRST_NAME + " " + HIDDEN_LAST_NAME;

    private String playerId;
    private String firstName;
    private String lastName;
    private String shortName;
    private Visibility visibility;
    private Integer validLapsCount;
    private Integer invalidLapsCount;
    private Long driveTimeMillis;
    private Instant lastActivity;

    public String getFullName() {
        if (firstName == null || lastName == null) {
            return UNKNOWN_DRIVER;
        }

        if (visibility == Visibility.PRIVATE) {
            return HIDDEN_FULL_NAME;
        }

        return String.join(" ", firstName, lastName);
    }

    public String getFirstName() {
        if (firstName == null) {
            return UNKNOWN_DRIVER;
        }

        if (visibility == Visibility.PRIVATE) {
            return HIDDEN_FIRST_NAME;
        }

        return firstName;
    }

    public String getLastName() {
        if (lastName == null) {
            return UNKNOWN_DRIVER;
        }

        if (visibility == Visibility.PRIVATE) {
            return HIDDEN_LAST_NAME;
        }

        return lastName;
    }

    public String getShortName() {
        if (shortName == null) {
            return UNKNOWN_DRIVER;
        }

        if (visibility == Visibility.PRIVATE) {
            return HIDDEN_SHORT_NAME;
        }

        return shortName;
    }

    @SuppressWarnings("unused")
    public String getPrettyDriveTime() {
        return FormatUtils.formatDriveTime(driveTimeMillis);
    }
    
    public String toString() {
        return getFullName();
    }
}
