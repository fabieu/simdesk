package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.SessionInfo;

public class SessionEvent extends LiveTimingEvent<SessionInfo> {
    public SessionEvent(SessionInfo sessionInfo, String dashboardId) {
        super(sessionInfo, dashboardId);
    }
}