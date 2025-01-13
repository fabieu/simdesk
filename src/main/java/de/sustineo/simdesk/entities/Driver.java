package de.sustineo.simdesk.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "driver")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Driver extends Model {
    private static final String UNKNOWN_DRIVER = "Unknown Driver";
    private static final String HIDDEN_FIRST_NAME = "Hidden";
    private static final String HIDDEN_LAST_NAME = "Driver";
    private static final String HIDDEN_SHORT_NAME = "HDR";
    private static final String HIDDEN_FULL_NAME = HIDDEN_FIRST_NAME + " " + HIDDEN_LAST_NAME;

    @Id
    @Column(name = "player_id")
    private String playerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "last_activity")
    private Instant lastActivity;

    @CreationTimestamp
    @Column(name = "insert_datetime")
    private Instant insertDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private Visibility visibility;

    public String getFullName() {
        if (firstName == null || lastName == null) {
            return UNKNOWN_DRIVER;
        }

        return String.format("%s %s", firstName, lastName);
    }

    public String getFullNameCensored() {
        if (visibility == Visibility.PRIVATE) {
            return HIDDEN_FULL_NAME;
        }

        return getFullName();
    }

    /**
     * The Grid editor needs to know what has changed in order to close the right thing.
     * Make sure that equals and hashCode of <code>Driver</code> uses unique attributes.
     */
    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Driver driver)) return false;

        return playerId.equals(driver.playerId);
    }

    @Override
    public int hashCode() {
        return playerId.hashCode();
    }

    @Override
    public String toString() {
        return getFullNameCensored();
    }
}
