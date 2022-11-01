package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import de.sustineo.acc.leaderboard.entities.enums.CupCategory;
import de.sustineo.acc.leaderboard.entities.enums.DriverCategory;
import lombok.Data;

@Data
public class LeaderboardLine {
    private Integer id;
    private Integer sessionId;
    private Integer carId;
    private Integer carNumber;
    private String carModel;
    private CarGroup carGroup;
    private CupCategory cupCategory;
    private String driverId;
    private String driverName;
    private String driverShortName;
    private DriverCategory driverCategory;
    private String teamName;
    private String nationality;
    private Integer lapCountValid;
    private Integer lapCountInvalid;
    private Long lapTime;
    private Long split1;
    private Long split2;
    private Long split3;
}
