package de.sustineo.simdesk.services.livetiming;

import de.sustineo.simdesk.entities.livetiming.DashboardState;
import de.sustineo.simdesk.entities.livetiming.protocol.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log
@Service
@RequiredArgsConstructor
public class LiveTimingStateService {
    private final LiveTimingRequestService liveTimingRequestService;

    private final Map<String, DashboardState> dashboardStates = new ConcurrentHashMap<>();

    public DashboardState getDashboardState(String dashboardId) {
        return dashboardStates.computeIfAbsent(dashboardId, DashboardState::new);
    }

    public void updateDashboardState(DashboardState dashboardState) {
        dashboardStates.put(dashboardState.getDashboardId(), dashboardState);
    }

    public void handleRegistrationResult(String sessionId, String dashboardId, int connectionID, boolean connectionSuccess, boolean isReadonly, String errorMessage) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        //TODO: Handle registration result logic
        updateDashboardState(dashboardState);
    }

    public void handleRealtimeUpdate(String sessionId, String dashboardId, SessionInfo sessionInfo) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        // TODO:Handle realtime update logic
        updateDashboardState(dashboardState);
    }

    public void handleRealtimeCarUpdate(String sessionId, String dashboardId, RealtimeInfo realtimeInfo) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        //TODO: Handle realtime car update logic
        updateDashboardState(dashboardState);
    }

    public void handleEntryListUpdate(String sessionId, String dashboardId, List<Integer> cars) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        //TODO: Handle entry list update logic
        updateDashboardState(dashboardState);
    }

    public void handleEntrylistCarUpdate(String sessionId, String dashboardId, CarInfo carInfo) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        //TODO: Handle entry list car update logic
        updateDashboardState(dashboardState);
    }

    public void handleBroadcastingEvent(String sessionId, String dashboardId, BroadcastingEvent broadcastingEvent) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        //TODO: Handle broadcasting event logic
        updateDashboardState(dashboardState);
    }

    public void handleTrackData(String sessionId, String dashboardId, TrackInfo trackInfo) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        //TODO: Handle track data logic
        updateDashboardState(dashboardState);
    }
}
