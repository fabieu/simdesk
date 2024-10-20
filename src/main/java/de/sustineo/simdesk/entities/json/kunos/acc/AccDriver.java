package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccDriver {
    public AccDriver(AccDriver other) {
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.shortName = other.shortName;
        this.driverCategory = other.driverCategory;
        this.playerId = other.playerId;
        this.nationality = other.nationality;
    }

    private String firstName;
    private String lastName;
    private String shortName;
    private AccDriverCategory driverCategory;
    @JsonProperty("playerId")
    @JsonAlias("playerID")
    @NotNull
    private String playerId;
    private AccNationality nationality;
}
