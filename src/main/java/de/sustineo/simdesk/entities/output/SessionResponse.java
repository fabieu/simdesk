package de.sustineo.simdesk.entities.output;

import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.SessionType;
import lombok.Data;

import java.time.Instant;

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
    }
}
