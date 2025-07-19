package de.sustineo.simdesk.entities.livetiming;

import lombok.Data;
import lombok.extern.java.Log;

import java.time.Instant;

@Log
@Data
public class DashboardState {
    private final String dashboardId;

    private final SessionInfo sessionInfo = new SessionInfo();
    private Instant lastEntryListUpdate = Instant.now();

    public DashboardState(String dashboardId) {
        this.dashboardId = dashboardId;
    }
}
