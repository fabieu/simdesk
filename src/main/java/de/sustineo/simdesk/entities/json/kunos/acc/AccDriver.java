package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sustineo.simdesk.entities.Driver;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccDriver {
    private String firstName;
    private String lastName;
    private String shortName;
    private AccDriverCategory driverCategory;
    @JsonProperty("playerID")
    @JsonAlias("playerId")
    @NotNull
    private String playerId;
    private AccNationality nationality;

    public AccDriver(@Nonnull AccDriver other) {
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.shortName = other.shortName;
        this.driverCategory = other.driverCategory;
        this.playerId = other.playerId;
        this.nationality = other.nationality;
    }

    public AccDriver(@Nonnull Driver driver) {
        this.firstName = driver.getFirstName();
        this.lastName = driver.getLastName();
        this.shortName = driver.getShortName();
        this.playerId = "S" + driver.getId();
    }
}
