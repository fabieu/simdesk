package de.sustineo.simdesk.services.livetiming;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.livetiming.*;
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

    public void handleRegistrationResult(String sessionId, String dashboardId, int connectionID, boolean connectionSuccess, boolean isReadonly, String errorMessage) {
        log.info("Connection ID: " + connectionID + " Connection Success: " + connectionSuccess + " Readonly: " + isReadonly + " Error Message: " + errorMessage);

        Instant now = Instant.now();
        boolean requestEntrylist;

        DashboardState dashboardState = getDashboardState(dashboardId);
        synchronized (dashboardState) {
            dashboardState.getConnections().put(sessionId, connectionID);

            requestEntrylist = now.minusSeconds(5).isAfter(dashboardState.getLastEntryListUpdate());
            if (requestEntrylist) {
                dashboardState.setLastEntryListUpdate(now);
            }

            updateDashboardState(dashboardState);
        }

        if (requestEntrylist) {
            liveTimingRequestService.sendEntrylistRequest(sessionId, connectionID);
        }

        liveTimingRequestService.sendTrackDataRequest(sessionId, connectionID);
    }

    public void handleRealtimeUpdate(String sessionId, String dashboardId, SessionInfo sessionInfo) {
        log.info("Session Info: " + sessionInfo);

        DashboardState dashboardState = getDashboardState(dashboardId);
        synchronized (dashboardState) {
            dashboardState.setSessionInfo(sessionInfo);

            checkForDisconnects(dashboardState.getCars().values());

            updateDashboardState(dashboardState);
        }

        eventPublisher.publishEvent(LiveTimingEvent.of(sessionInfo, dashboardId));
    }

    private void checkForDisconnects(Collection<CarInfo> cars) {
        Instant now = Instant.now();

        for (CarInfo car : cars) {
            if (car.isConnected()) {
                if (Duration.between(car.getLastUpdate(), now).compareTo(Duration.ofSeconds(10)) > 0) {
                    car.setConnected(false);

                    log.fine("Car " + car.getCarNumberString() + " received last update " + Duration.between(now, car.getLastUpdate()).toMillis() + "ms ago.");
                    log.info("Car disconnected: " + car.getCarNumberString() + "\t" + car.getDriver().getFullName());
                    //TODO: Publish car disconnected event
                }
            }
        }
    }

    public void handleRealtimeCarUpdate(String sessionId, String dashboardId, RealtimeInfo realtimeInfo) {
        log.info("Realtime Info: " + realtimeInfo);

        boolean requestEntrylist = false;
        Integer connectionId;

        DashboardState dashboardState = getDashboardState(dashboardId);
        synchronized (dashboardState) {
            connectionId = dashboardState.getConnections().get(sessionId);

            Optional<CarInfo> carInfo = dashboardState.getCarInfo(realtimeInfo.getCarId());
            if (carInfo.isPresent()) {
                CarInfo currentCarInfo = carInfo.get();
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
                log.info(String.format("Realtime update for unknown car: %s. Sending entrylist request.", realtimeInfo.getCarId()));
                requestEntrylist = true;
            }
        }

        if (requestEntrylist && connectionId != null) {
            liveTimingRequestService.sendEntrylistRequest(sessionId, connectionId);
        }

        eventPublisher.publishEvent(LiveTimingEvent.of(realtimeInfo, dashboardId));
    }

    public void handleEntryListUpdate(String sessionId, String dashboardId, List<Integer> cars) {
        log.info("Entrylist update for cars: " + cars);

        eventPublisher.publishEvent(LiveTimingEvent.of(cars, dashboardId));
    }

    public void handleEntrylistCarUpdate(String sessionId, String dashboardId, CarInfo carInfo) {
        log.info("Car Info: " + carInfo);

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

            dashboardState.setCarInfo(currentCarInfo.getId(),  currentCarInfo);

            updateDashboardState(dashboardState);
        }

        if (newConnection) {
            log.info(String.format("Car connected: #%s \t %s", carInfo.getCarNumber(), carInfo.getDriver().getFullName()));
            //TODO: Publish car connected event
        }

        eventPublisher.publishEvent(LiveTimingEvent.of(carInfo, dashboardId));
    }

    public void handleBroadcastingEvent(String sessionId, String dashboardId, BroadcastingInfo broadcastingInfo) {
        log.info("Broadcasting Event: " + broadcastingInfo);

        eventPublisher.publishEvent(LiveTimingEvent.of(broadcastingInfo, dashboardId));
    }

    public void handleTrackData(String sessionId, String dashboardId, TrackInfo trackInfo) {
        log.info("Track Info: " + trackInfo);

        DashboardState dashboardState = getDashboardState(dashboardId);
        synchronized (dashboardState) {
            dashboardState.setTrackInfo(trackInfo);
            updateDashboardState(dashboardState);
        }

        eventPublisher.publishEvent(LiveTimingEvent.of(trackInfo, dashboardId));
    }
}
