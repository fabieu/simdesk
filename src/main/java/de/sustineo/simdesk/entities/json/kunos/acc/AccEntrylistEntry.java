package de.sustineo.simdesk.entities.json.kunos.acc;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AccEntrylistEntry {
    public static final int DEFAULT_RACE_NUMBER = -1;
    public static final int DEFAULT_FORCED_CAR_MODEL = -1;
    public static final int DEFAULT_OVERRIDE_DRIVER_INFO = 0;
    public static final int DEFAULT_DEFAULT_GRID_POSITION = -1;
    public static final int DEFAULT_BALLAST_KG = 0;
    public static final int DEFAULT_RESTRICTOR = 0;
    public static final String DEFAULT_CUSTOM_CAR = "";
    public static final int DEFAULT_OVERRIDE_CAR_MODEL_FOR_CUSTOM_CAR = 0;
    public static final int DEFAULT_IS_SERVER_ADMIN = 0;

    public static final int MAX_DRIVERS = 5;

    public AccEntrylistEntry(AccEntrylistEntry other) {
        this.drivers = other.drivers;
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
    private List<@Valid AccDriver> drivers = new ArrayList<>(List.of(new AccDriver()));
    private Integer raceNumber = DEFAULT_RACE_NUMBER;
    private Integer forcedCarModel = DEFAULT_FORCED_CAR_MODEL;
    private Integer overrideDriverInfo = DEFAULT_OVERRIDE_DRIVER_INFO;
    private Integer defaultGridPosition = DEFAULT_DEFAULT_GRID_POSITION;
    private Integer ballastKg = DEFAULT_BALLAST_KG;
    private Integer restrictor = DEFAULT_RESTRICTOR;
    private String customCar = DEFAULT_CUSTOM_CAR;
    private Integer overrideCarModelForCustomCar = DEFAULT_OVERRIDE_CAR_MODEL_FOR_CUSTOM_CAR;
    private Integer isServerAdmin = DEFAULT_IS_SERVER_ADMIN;
}
