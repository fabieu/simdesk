package de.sustineo.acc.leaderboard.entities.entrylist;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class Entry {
    @NotEmpty
    private List<@Valid Driver> drivers;
    @NotNull
    private Integer raceNumber;
    @NotNull
    private Integer forcedCarModel;
    @NotNull
    private Integer overrideDriverInfo;
    @NotNull
    private Integer defaultGridPosition;
    @NotNull
    private Integer ballastKg;
    @NotNull
    private Integer restrictor;
    @NotNull
    private String customCar;
    @NotNull
    private Integer overrideCarModelForCustomCar;
    @NotNull
    private Integer isServerAdmin;
}
