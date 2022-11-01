package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.SessionType;
import lombok.Data;

@Data
public class Session {
    private Integer id;
    private SessionType sessionType;
    private Integer raceWeekendIndex;
    private String serverName;
    private Boolean wetSession;
    private Integer driverCount;
}
