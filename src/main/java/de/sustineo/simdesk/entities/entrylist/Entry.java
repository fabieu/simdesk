package de.sustineo.simdesk.entities.entrylist;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class Entry {
    @NotEmpty
    private List<@Valid Driver> drivers;
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
