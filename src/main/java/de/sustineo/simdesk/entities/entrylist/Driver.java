package de.sustineo.simdesk.entities.entrylist;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Driver {
    private String firstName;
    private String lastName;
    private String shortName;
    private Integer driverCategory;
    @JsonProperty("playerID")
    @NotNull
    private String playerId;
    private String nationality;
}
