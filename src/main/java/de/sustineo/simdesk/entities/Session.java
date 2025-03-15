package de.sustineo.simdesk.entities;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Session {
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
    private String fileContent;
    private Instant insertDatetime;

    public String getTrackName() {
        return Track.getTrackNameByAccId(trackId);
    }
}
