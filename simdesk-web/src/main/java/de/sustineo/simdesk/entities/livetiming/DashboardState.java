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

    /**
     * Checks if an entry list request should be made based on the last update time.
     * If more than 5 seconds have passed since the last update, it returns true.
     * Otherwise, it returns false.
     *
     * @return true if an entry list request may be made, false otherwise
     */
    public boolean mayRequestEntrylist() {
        Instant now = Instant.now();

        // Request entry list if it has been more than 5 seconds since the last update
        boolean mayRequestEntrylist = now.minusSeconds(5).isAfter(lastEntryListUpdate);

        if (mayRequestEntrylist) {
            lastEntryListUpdate = now;
        }

        return mayRequestEntrylist;
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