package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.SessionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Session {
    private Integer id;
    private SessionType sessionType;
    private Integer raceWeekendIndex;
    private String serverName;
    private Boolean wetSession;
    private Integer driverCount;
    private String fileChecksum;
    private String fileName;
    private String fileDirectory;
}
