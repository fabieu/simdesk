package de.sustineo.simdesk.services.livetiming;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.livetiming.*;
import de.sustineo.simdesk.entities.livetiming.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Profile(ProfileManager.PROFILE_LIVE_TIMING)
@Log
@Service
@RequiredArgsConstructor
public class LiveTimingStateService {
    private final LiveTimingRequestService liveTimingRequestService;
    private final ApplicationEventPublisher eventPublisher;

    private final Map<String, DashboardState> dashboardStates = new ConcurrentHashMap<>();

    public DashboardState getDashboardState(String dashboardId) {
        return dashboardStates.computeIfAbsent(dashboardId, DashboardState::new);
    }

    public void updateDashboardState(DashboardState dashboardState) {
        dashboardStates.put(dashboardState.getDashboardId(), dashboardState);
    }

    public void handleRegistrationResult(String sessionId, String dashboardId, int connectionID, boolean connectionSuccess, boolean readOnly, String errorMessage) {
        log.fine("Connection ID: " + connectionID + " Connection Success: " + connectionSuccess + " Read-only: " + readOnly + " Error Message: " + errorMessage);

        DashboardState dashboardState = getDashboardState(dashboardId);
        synchronized (dashboardState) {
            dashboardState.getConnections().put(sessionId, connectionID);

            updateDashboardState(dashboardState);
        }

        liveTimingRequestService.sendEntrylistRequest(sessionId, connectionID);
        liveTimingRequestService.sendTrackDataRequest(sessionId, connectionID);
    }

    public void handleRealtimeUpdate(String sessionId, String dashboardId, SessionInfo sessionInfo) {
        log.fine(String.format("Received realtime update for dashboard %s: %s", dashboardId, sessionInfo));

        DashboardState dashboardState = getDashboardState(dashboardId);
        synchronized (dashboardState) {
            dashboardState.setSessionInfo(sessionInfo);
            dashboardState.getBroadcastingInfos().clear(); // Clear previous broadcasting infos

            checkForDisconnect(dashboardId, dashboardState.getCars().values());

            updateDashboardState(dashboardState);
        }

        eventPublisher.publishEvent(new SessionEvent(sessionInfo, dashboardId));
    }

    private void checkForDisconnect(String dashboardId, Collection<CarInfo> carInfos) {
        Instant now = Instant.now();

        for (CarInfo carInfo : carInfos) {
            log.finest(String.format("Car %s received last update %sms ago.", carInfo.getCarNumberString(), Duration.between(now, carInfo.getLastUpdate()).toMillis()));

            if (carInfo.isConnected()) {
                if (Duration.between(carInfo.getLastUpdate(), now).compareTo(Duration.ofSeconds(10)) > 0) {
                    carInfo.setConnected(false);

                    log.info(String.format("Car %s with driver %s disconnected.", carInfo.getCarNumberString(), carInfo.getDriver().getFullName()));
                    eventPublisher.publishEvent(new CarDisconnectedEvent(carInfo, dashboardId));
                }
            }
        }
    }

    public void handleRealtimeCarUpdate(String sessionId, String dashboardId, RealtimeInfo realtimeInfo) {
        log.fine(String.format("Received realtime car update for dashboard %s: %s", dashboardId, realtimeInfo));

        boolean requestEntrylist = false;
        CarInfo currentCarInfo = null;
        Integer connectionId;

        DashboardState dashboardState = getDashboardState(dashboardId);
        synchronized (dashboardState) {
            connectionId = dashboardState.getConnections().get(sessionId);

            Optional<CarInfo> carInfo = dashboardState.getCarInfo(realtimeInfo.getCarId());
            if (carInfo.isPresent()) {
                currentCarInfo = carInfo.get();
                currentCarInfo.setLastUpdate(Instant.now());
                currentCarInfo.setConnected(true);
                currentCarInfo.setDriverIndexRealtime(realtimeInfo.getDriverIndex());
                currentCarInfo.setDriverCount(realtimeInfo.getDriverCount());
                currentCarInfo.setGear(realtimeInfo.getGear());
                currentCarInfo.setYaw(realtimeInfo.getYaw());
                currentCarInfo.setPitch(realtimeInfo.getPitch());
                currentCarInfo.setRoll(realtimeInfo.getRoll());
                currentCarInfo.setAccCarLocation(currentCarInfo.getAccCarLocation());
                currentCarInfo.setKmh(realtimeInfo.getKmh());
                currentCarInfo.setPosition(realtimeInfo.getPosition());
                currentCarInfo.setCupPosition(realtimeInfo.getCupPosition());
                currentCarInfo.setTrackPosition(realtimeInfo.getTrackPosition());
                currentCarInfo.setSplinePosition(realtimeInfo.getSplinePosition());
                currentCarInfo.setLapCount(realtimeInfo.getLaps());
                currentCarInfo.setDelta(realtimeInfo.getDelta());
                currentCarInfo.setBestLap(realtimeInfo.getBestSessionLap());
                currentCarInfo.setLastLap(realtimeInfo.getLastLap());
                currentCarInfo.setCurrentLap(realtimeInfo.getCurrentLap());

                dashboardState.setCarInfo(carInfo.get().getId(), currentCarInfo);
                updateDashboardState(dashboardState);
            } else {
                requestEntrylist = dashboardState.shouldRequestEntrylist();
            }
        }

        if (requestEntrylist && connectionId != null) {
            log.info(String.format("Received update for unknown car %s, requesting entry list for dashboard %s", realtimeInfo.getCarId(), dashboardId));
            liveTimingRequestService.sendEntrylistRequest(sessionId, connectionId);
        }

        if (currentCarInfo != null) {
            eventPublisher.publishEvent(new CarEvent(currentCarInfo, dashboardId));
        }
    }

    public void handleEntryListUpdate(String sessionId, String dashboardId, List<Integer> cars) {
        log.fine(String.format("Received entry list update for dashboard %s: %s", dashboardId, cars));
    }

    public void handleEntrylistCarUpdate(String sessionId, String dashboardId, CarInfo carInfo) {
        log.fine(String.format("Received entrylist car update for dashboard %s: %s", dashboardId, carInfo));

        boolean newConnection;

        DashboardState dashboardState = getDashboardState(dashboardId);
        synchronized (dashboardState) {
            CarInfo currentCarInfo = dashboardState.getCarInfo(carInfo.getId()).orElseGet(CarInfo::new);

            newConnection = !currentCarInfo.isConnected();

            currentCarInfo.setLastUpdate(Instant.now());
            currentCarInfo.setConnected(true);
            currentCarInfo.setId(carInfo.getId());
            currentCarInfo.setCar(carInfo.getCar());
            currentCarInfo.setTeamName(carInfo.getTeamName());
            currentCarInfo.setCarNumber(carInfo.getCarNumber());
            currentCarInfo.setCupCategory(carInfo.getCupCategory());
            currentCarInfo.setDriverIndexRealtime(carInfo.getCurrentDriverIndex());
            currentCarInfo.setNationality(carInfo.getNationality());
            currentCarInfo.setDrivers(carInfo.getDrivers());

            dashboardState.setCarInfo(currentCarInfo.getId(), currentCarInfo);

            updateDashboardState(dashboardState);
        }

        if (newConnection) {
            log.info(String.format("Car connected: %s \t %s", carInfo.getCarNumberString(), carInfo.getDriver().getFullName()));
            eventPublisher.publishEvent(new CarConnectedEvent(carInfo, dashboardId));
        }

        eventPublisher.publishEvent(new CarEvent(carInfo, dashboardId));
    }

    public void handleBroadcastingEvent(String sessionId, String dashboardId, BroadcastingInfo broadcastingInfo) {
        log.fine(String.format("Received broadcasting event for dashboard %s: %s", dashboardId, broadcastingInfo));

        DashboardState dashboardState = getDashboardState(dashboardId);
        synchronized (dashboardState) {
            dashboardState.addBroadcastingInfo(broadcastingInfo);
            updateDashboardState(dashboardState);
        }

        eventPublisher.publishEvent(new BroadcastingEvent(broadcastingInfo, dashboardId));
    }

    public void handleTrackData(String sessionId, String dashboardId, TrackInfo trackInfo) {
        log.fine(String.format("Received track info for dashboard %s: %s", dashboardId, trackInfo));

        DashboardState dashboardState = getDashboardState(dashboardId);
        synchronized (dashboardState) {
            dashboardState.setTrackInfo(trackInfo);
            updateDashboardState(dashboardState);
        }

        eventPublisher.publishEvent(new TrackEvent(trackInfo, dashboardId));
    }
}