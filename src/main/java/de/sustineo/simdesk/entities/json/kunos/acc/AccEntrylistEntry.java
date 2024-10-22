package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
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


    @JsonIgnore
    private UUID uuid;
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

    public AccEntrylistEntry() {
        this.uuid = UUID.randomUUID();
        this.drivers = new ArrayList<>(List.of(new AccDriver()));
        this.raceNumber = DEFAULT_RACE_NUMBER;
        this.forcedCarModel = DEFAULT_FORCED_CAR_MODEL;
        this.overrideDriverInfo = DEFAULT_OVERRIDE_DRIVER_INFO;
        this.defaultGridPosition = DEFAULT_DEFAULT_GRID_POSITION;
        this.ballastKg = DEFAULT_BALLAST_KG;
        this.restrictor = DEFAULT_RESTRICTOR;
        this.customCar = DEFAULT_CUSTOM_CAR;
        this.overrideCarModelForCustomCar = DEFAULT_OVERRIDE_CAR_MODEL_FOR_CUSTOM_CAR;
        this.isServerAdmin = DEFAULT_IS_SERVER_ADMIN;
    }

    public AccEntrylistEntry(AccEntrylistEntry other) {
        this.uuid = UUID.randomUUID();
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
}
