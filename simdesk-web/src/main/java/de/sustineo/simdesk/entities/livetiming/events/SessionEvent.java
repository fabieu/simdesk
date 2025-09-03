package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.SessionInfo;

public class SessionEvent extends LiveTimingEvent<SessionInfo> {
    public SessionEvent(String dashboardId, SessionInfo sessionInfo) {
        super(dashboardId, sessionInfo);
    }
}