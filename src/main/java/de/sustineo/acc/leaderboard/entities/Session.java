package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.SessionType;
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
    private String trackId;
    private Boolean wetSession;
    private Integer driverCount;
    private String fileChecksum;
    private String fileName;
    private String fileDirectory;
    private Boolean importSuccess;

    public String getDescription() {
        return String.format("%s - %s", sessionType.getDescription(), serverName);
    }
}
