package de.sustineo.simdesk.entities.json.kunos;

import de.sustineo.simdesk.entities.CupCategory;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class AccCar {
    private Integer carId;
    private Integer raceNumber;
    private Integer carModel;
    private CupCategory cupCategory;
    private String carGroup; // unused
    private String teamName;
    private Integer nationality;
    private Integer carGuid;
    private Integer teamGuid;
    private Integer ballastKg;
    private List<AccDriver> drivers;

    public Optional<AccDriver> getDriverByIndex(int index) {
        return Optional.ofNullable(drivers.get(index));
    }
}
