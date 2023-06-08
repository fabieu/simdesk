package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.SessionType;
import de.sustineo.acc.leaderboard.entities.enums.Track;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    private Integer id;
    private SessionType sessionType;
    private Integer raceWeekendIndex;
    private String serverName;
    private Track trackId;
    private Boolean wetSession;
    private Integer driverCount;
    private String fileChecksum;
    private String fileName;
    private String fileDirectory;
    private Boolean importSuccess;
}
