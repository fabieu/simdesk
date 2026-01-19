package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccDriverCategory;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccNationality;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccDriver {
    private String firstName;
    private String lastName;
    private String shortName;
    private AccDriverCategory driverCategory;
    @JsonProperty("playerID")
    @JsonAlias("playerId")
    @NotNull
    private String playerId;
    private AccNationality nationality;

    public static AccDriver create() {
        return new AccDriver();
    }

    public static AccDriver create(@Nonnull AccDriver other) {
        AccDriver accDriver = new AccDriver();
        accDriver.firstName = other.firstName;
        accDriver.lastName = other.lastName;
        accDriver.shortName = other.shortName;
        accDriver.driverCategory = other.driverCategory;
        accDriver.playerId = other.playerId;
        accDriver.nationality = other.nationality;
        return accDriver;
    }

    public static AccDriver create(@Nonnull Driver driver) {
        AccDriver accDriver = new AccDriver();
        accDriver.firstName = driver.getFirstName();
        accDriver.lastName = driver.getLastName();
        accDriver.shortName = driver.getShortName();
        accDriver.playerId = "S" + driver.getId();
        return accDriver;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof AccDriver accDriver)) return false;

        return Objects.equals(playerId, accDriver.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerId);
    }
}
