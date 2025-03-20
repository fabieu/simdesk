package de.sustineo.simdesk.entities.output;

import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.SessionType;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class SessionResponse {
    private Integer id;
    private Instant sessionDatetime;
    private SessionType sessionType;
    private String serverName;
    private String trackId;
    private Boolean wetSession;
    private Integer carCount;
    private String fileName;
    private String fileChecksum;
    private Instant insertDatetime;
    private List<LapResponse> laps;

    public SessionResponse(Session session) {
        this.id = session.getId();
        this.sessionDatetime = session.getSessionDatetime();
        this.sessionType = session.getSessionType();
        this.serverName = session.getServerName();
        this.trackId = session.getTrackId();
        this.wetSession = session.getWetSession();
        this.carCount = session.getCarCount();
        this.fileName = session.getFileName();
        this.fileChecksum = session.getFileChecksum();
        this.insertDatetime = session.getInsertDatetime();
    }

    public void setLaps(List<Lap> laps) {
        if (laps == null) {
            return;
        }

        this.laps = laps.stream()
                .map(LapResponse::new)
                .toList();
    }
}
