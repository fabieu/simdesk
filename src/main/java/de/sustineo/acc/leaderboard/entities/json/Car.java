package de.sustineo.acc.leaderboard.entities.json;

import lombok.Data;

import java.util.List;

@Data
public class Car {
    private Integer carId;
    private Integer raceNumber;
    private Integer carModel;
    private Integer cupCategory;
    private CarGroup carGroup;
    private String teamName;
    private Integer nationality;
    private Integer carGuid;
    private Integer teamGuid;
    private List<Driver> drivers;
}
