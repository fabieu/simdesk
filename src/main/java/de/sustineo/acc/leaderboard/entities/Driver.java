package de.sustineo.acc.leaderboard.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {
    private String playerId;
    private String firstName;
    private String lastName;
    private String shortName;

    public String getFullName() {
        if (firstName == null || lastName== null) {
            return null;
        }

        String driverFullName = String.join(" ", firstName, lastName);

        if (shortName == null) {
            return driverFullName;
        } else {
            return driverFullName + " (" + shortName + ")";
        }
    }
}
