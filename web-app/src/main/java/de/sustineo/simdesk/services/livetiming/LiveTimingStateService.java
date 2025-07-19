package de.sustineo.simdesk.services.livetiming;

import de.sustineo.simdesk.entities.livetiming.*;
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
        log.info("Connection ID: " + connectionID +
                " Connection Success: " + connectionSuccess + " Readonly: " + isReadonly + " Error Message: " + errorMessage);
        updateDashboardState(dashboardState);
    }

    public void handleRealtimeUpdate(String sessionId, String dashboardId, SessionInfo sessionInfo) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        // TODO:Handle realtime update logic
        log.info("Session Info: " + sessionInfo);
        updateDashboardState(dashboardState);
    }

    public void handleRealtimeCarUpdate(String sessionId, String dashboardId, RealtimeInfo realtimeInfo) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        //TODO: Handle realtime car update logic
        log.info("Realtime Info: " + realtimeInfo);
        updateDashboardState(dashboardState);
    }

    public void handleEntryListUpdate(String sessionId, String dashboardId, List<Integer> cars) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        //TODO: Handle entry list update logic
        log.info("Cars: " + cars);
        updateDashboardState(dashboardState);
    }

    public void handleEntrylistCarUpdate(String sessionId, String dashboardId, CarInfo carInfo) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        //TODO: Handle entry list car update logic
        log.info("Car Info: " + carInfo);
        updateDashboardState(dashboardState);
    }

    public void handleBroadcastingEvent(String sessionId, String dashboardId, BroadcastingInfo broadcastingInfo) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        //TODO: Handle broadcasting event logic
        log.info("Broadcasting Event: " + broadcastingInfo);
        updateDashboardState(dashboardState);
    }

    public void handleTrackData(String sessionId, String dashboardId, TrackInfo trackInfo) {
        DashboardState dashboardState = getDashboardState(dashboardId);
        //TODO: Handle track data logic
        log.info("Track Info: " + trackInfo);
        updateDashboardState(dashboardState);
    }
}
