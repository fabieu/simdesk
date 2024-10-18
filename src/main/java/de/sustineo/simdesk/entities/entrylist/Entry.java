package de.sustineo.simdesk.entities.entrylist;

import de.sustineo.simdesk.entities.json.kunos.AccDriver;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class Entry {
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
