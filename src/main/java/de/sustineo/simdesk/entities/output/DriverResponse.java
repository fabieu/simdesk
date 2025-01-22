package de.sustineo.simdesk.entities.output;

import de.sustineo.simdesk.entities.Driver;
import lombok.Data;

import java.time.Instant;

@Data
public class DriverResponse {
    private String playerId;
    private String firstName;
    private String lastName;
    private String shortName;
    private Instant lastActivity;

    public DriverResponse(Driver driver) {
        this.playerId = driver.getPlayerId();
        this.firstName = driver.getFirstName();
        this.lastName = driver.getLastName();
        this.shortName = driver.getShortName();
        this.lastActivity = driver.getLastActivity();
    }
}
