package de.sustineo.simdesk.entities.livetiming;

import lombok.Data;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class DashboardState {
    private final String dashboardId;

    private Map<String, Integer> connections = new ConcurrentHashMap<>();
    private Map<Integer, CarInfo> cars = new ConcurrentHashMap<>();
    private SessionInfo sessionInfo;
    private TrackInfo trackInfo;
    private Set<BroadcastingInfo> broadcastingInfos = new LinkedHashSet<>();

    private Instant lastEntryListUpdate = Instant.EPOCH;

    public DashboardState(String dashboardId) {
        this.dashboardId = dashboardId;
    }

    public Optional<CarInfo> getCarInfo(int carId) {
        return Optional.ofNullable(cars.get(carId));
    }

    public void setCarInfo(int carId, CarInfo carInfo) {
        cars.put(carId, carInfo);
    }

    public void addBroadcastingInfo(BroadcastingInfo broadcastingInfo) {
        broadcastingInfos.add(broadcastingInfo);
    }
}
