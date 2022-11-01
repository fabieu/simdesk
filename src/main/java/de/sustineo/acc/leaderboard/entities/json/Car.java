package de.sustineo.acc.leaderboard.entities.json;

import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import de.sustineo.acc.leaderboard.entities.enums.CupCategory;
import lombok.Data;

import java.util.List;

@Data
public class Car {
    private Integer carId;
    private Integer raceNumber;
    private Integer carModel;
    private CupCategory cupCategory;
    private CarGroup carGroup;
    private String teamName;
    private Integer nationality;
    private Integer carGuid;
    private Integer teamGuid;
    private List<Driver> drivers;
}
