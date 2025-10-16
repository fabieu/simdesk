package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCupCategory;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public final class AccTeam {
    @JsonProperty("carId")
    private Integer teamId;
    private Integer raceNumber;
    @JsonProperty("carModel")
    private Integer carModelId;
    private AccCupCategory cupCategory;
    private String carGroup; // unused, calculated from carId
    private String teamName;
    private Integer nationality;
    private Integer carGuid;
    private Integer teamGuid;
    private Integer ballastKg;
    private List<AccDriver> drivers;

    public Optional<AccDriver> getDriverByIndex(int index) {
        return Optional.ofNullable(drivers)
                .map(accDrivers -> accDrivers.get(index));
    }
}
