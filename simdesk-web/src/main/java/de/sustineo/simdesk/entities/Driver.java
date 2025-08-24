package de.sustineo.simdesk.entities;

import de.sustineo.simdesk.utils.FormatUtils;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Driver {
    private static final String UNKNOWN_DRIVER = "Unknown Driver";
    private static final String HIDDEN_FIRST_NAME = "Hidden";
    private static final String HIDDEN_LAST_NAME = "Driver";
    private static final String HIDDEN_SHORT_NAME = "HDR";
    private static final String HIDDEN_FULL_NAME = HIDDEN_FIRST_NAME + " " + HIDDEN_LAST_NAME;

    private String id;
    private String firstName;
    private String lastName;
    private String shortName;
    private Visibility visibility;
    private Integer validLapsCount;
    private Integer invalidLapsCount;
    private Long driveTimeMillis;
    private Instant lastActivity;

    public String getFullName() {
        if (visibility == Visibility.PRIVATE) {
            return HIDDEN_FULL_NAME;
        }

        return getRealName();
    }

    public String getRealName() {
        if (firstName == null || lastName == null) {
            return UNKNOWN_DRIVER;
        }

        return String.format("%s %s", firstName, lastName);
    }

    @SuppressWarnings("unused")
    public String getPrettyDriveTime() {
        return FormatUtils.formatDriveTime(driveTimeMillis);
    }

    /**
     * The Grid editor needs to know what has changed in order to close the right thing.
     * Make sure that equals and hashCode of <code>Driver</code> uses unique attributes.
     */
    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Driver driver)) return false;

        return id.equals(driver.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
