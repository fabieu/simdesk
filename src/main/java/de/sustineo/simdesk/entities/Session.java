package de.sustineo.simdesk.entities;

import de.sustineo.simdesk.entities.enums.SessionType;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Session extends Entity {
    private Integer id;
    private SessionType sessionType;
    private Integer raceWeekendIndex;
    private String serverName;
    private String trackId;
    private Boolean wetSession;
    private Integer carCount;
    private Instant sessionDatetime;
    private String fileChecksum;
    private String fileName;
    private String fileDirectory;

    public String getTrackName() {
        return Track.getTrackNameById(trackId);
    }
}