package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.Track;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Session {
    private Integer id;
    private String sessionType;
    private Integer raceWeekendIndex;
    private String serverName;
    private Track trackName;
    private Boolean wetSession;
    private Integer driverCount;
    private String fileChecksum;
    private String fileName;
    private String fileDirectory;
}
