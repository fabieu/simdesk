package de.sustineo.simdesk.entities.json.kunos.acc;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccEntrylistEntry {
    public AccEntrylistEntry() {
        this.drivers = new ArrayList<>(List.of(new AccDriver()));
    }

    @NotEmpty
    private List<@Valid AccDriver> drivers;
    private Integer raceNumber;
    private Integer forcedCarModel;
    private Integer overrideDriverInfo;
    private Integer defaultGridPosition;
    private Integer ballastKg;
    private Integer restrictor;
    private String customCar;
    private Integer overrideCarModelForCustomCar;
    private Integer isServerAdmin;
}
