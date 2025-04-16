package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static AccEntrylistEntry create() {
        AccEntrylistEntry entry = new AccEntrylistEntry();
        entry.setUuid(UUID.randomUUID());
        entry.setDrivers(new ArrayList<>(List.of(new AccDriver())));
        entry.setRaceNumber(DEFAULT_RACE_NUMBER);
        entry.setForcedCarModel(DEFAULT_FORCED_CAR_MODEL);
        entry.setOverrideDriverInfo(DEFAULT_OVERRIDE_DRIVER_INFO);
        entry.setDefaultGridPosition(DEFAULT_DEFAULT_GRID_POSITION);
        entry.setBallastKg(DEFAULT_BALLAST_KG);
        entry.setRestrictor(DEFAULT_RESTRICTOR);
        entry.setCustomCar(DEFAULT_CUSTOM_CAR);
        entry.setOverrideCarModelForCustomCar(DEFAULT_OVERRIDE_CAR_MODEL_FOR_CUSTOM_CAR);
        entry.setIsServerAdmin(DEFAULT_IS_SERVER_ADMIN);
        return entry;
    }

    public static AccEntrylistEntry create(AccEntrylistEntry other) {
        AccEntrylistEntry entry = new AccEntrylistEntry();
        entry.setUuid(UUID.randomUUID());
        entry.setDrivers(new ArrayList<>(other.getDrivers()));
        entry.setRaceNumber(other.getRaceNumber());
        entry.setForcedCarModel(other.getForcedCarModel());
        entry.setOverrideDriverInfo(other.getOverrideDriverInfo());
        entry.setDefaultGridPosition(other.getDefaultGridPosition());
        entry.setBallastKg(other.getBallastKg());
        entry.setRestrictor(other.getRestrictor());
        entry.setCustomCar(other.getCustomCar());
        entry.setOverrideCarModelForCustomCar(other.getOverrideCarModelForCustomCar());
        entry.setIsServerAdmin(other.getIsServerAdmin());
        return entry;
    }

    public boolean hasDefaultGridPosition() {
        return defaultGridPosition != null && defaultGridPosition != DEFAULT_DEFAULT_GRID_POSITION;
    }
}
