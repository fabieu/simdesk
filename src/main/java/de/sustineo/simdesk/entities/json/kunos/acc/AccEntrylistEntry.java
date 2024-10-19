package de.sustineo.simdesk.entities.json.kunos.acc;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AccEntrylistEntry {
    public static final int MAX_DRIVERS = 5;

    public AccEntrylistEntry() {
        this.drivers = new ArrayList<>(List.of(new AccDriver()));
    }

    public AccEntrylistEntry(AccEntrylistEntry other) {
        if (other.drivers != null) {
            this.drivers = other.drivers.stream()
                    .map(AccDriver::new)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        this.raceNumber = other.raceNumber;
        this.forcedCarModel = other.forcedCarModel;
        this.overrideDriverInfo = other.overrideDriverInfo;
        this.defaultGridPosition = other.defaultGridPosition;
        this.ballastKg = other.ballastKg;
        this.restrictor = other.restrictor;
        this.customCar = other.customCar;
        this.overrideCarModelForCustomCar = other.overrideCarModelForCustomCar;
        this.isServerAdmin = other.isServerAdmin;
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
