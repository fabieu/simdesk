package de.sustineo.simdesk.client;

import de.sustineo.simdesk.client.model.Car;
import de.sustineo.simdesk.client.model.Session;
import de.sustineo.simdesk.client.protocol.SessionId;
import de.sustineo.simdesk.client.protocol.TrackInfo;
import lombok.*;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccBroadcastingState {
    private InetAddress hostAddress;
    private int hostPort;
    @Builder.Default
    private String displayName = "SimDesk";
    @Builder.Default
    private String connectionPassword = "";
    @Builder.Default
    private String commandPassword = "";
    private int updateInterval;
    @Builder.Default
    private SessionId currentSessionId = SessionId.dummy();
    @Builder.Default
    private int connectionId = -1;
    private boolean readOnly;
    @Builder.Default
    private boolean gameConnected = false;
    @Builder.Default
    private TrackInfo trackInfo = new TrackInfo();
    @Builder.Default
    private Session session = new Session();
    @Builder.Default
    private Map<Integer, Car> cars = new HashMap<>();

    public boolean hasCarWithIndex(int index) {
        return cars.containsKey(index);
    }

    public Collection<Car> getCars() {
        return cars.values();
    }

    public Optional<Car> getCar(int index) {
        return Optional.ofNullable(cars.get(index));
    }

    public void putCar(Car car) {
        cars.put(car.id, car);
    }

    public synchronized AccBroadcastingState copy() {
        AccBroadcastingState accBroadcastingState = new AccBroadcastingState();
        accBroadcastingState.hostAddress = this.hostAddress;
        accBroadcastingState.hostPort = this.hostPort;
        accBroadcastingState.displayName = this.displayName;
        accBroadcastingState.connectionPassword = this.connectionPassword;
        accBroadcastingState.commandPassword = this.commandPassword;
        accBroadcastingState.updateInterval = this.updateInterval;
        accBroadcastingState.currentSessionId = this.currentSessionId;
        accBroadcastingState.connectionId = this.connectionId;
        accBroadcastingState.readOnly = this.readOnly;
        accBroadcastingState.gameConnected = this.gameConnected;
        accBroadcastingState.trackInfo = this.trackInfo;
        accBroadcastingState.session = this.session.copy();
        accBroadcastingState.cars = this.cars.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().copy()));
        return accBroadcastingState;
    }
}