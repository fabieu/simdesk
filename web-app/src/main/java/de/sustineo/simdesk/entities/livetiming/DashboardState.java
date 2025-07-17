package de.sustineo.simdesk.entities.livetiming;

import de.sustineo.simdesk.entities.livetiming.protocol.enums.SessionPhase;
import de.sustineo.simdesk.entities.livetiming.protocol.enums.SessionType;
import lombok.Data;
import lombok.extern.java.Log;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Log
@Data
public class DashboardState {
    private final String dashboardId;
    private final Map<SessionType, Integer> sessionCounter = new HashMap<>();
    private SessionPhase sessionPhase = SessionPhase.NONE;
    private Instant lastEntryListUpdate = Instant.now();

    public DashboardState(String dashboardId) {
        this.dashboardId = dashboardId;
    }
}
