package de.sustineo.acc.leaderboard.entities;

import lombok.*;

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
