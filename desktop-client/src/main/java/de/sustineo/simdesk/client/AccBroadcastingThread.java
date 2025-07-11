package de.sustineo.simdesk.client;

import de.sustineo.simdesk.client.events.*;
import de.sustineo.simdesk.client.model.Car;
import de.sustineo.simdesk.client.model.Driver;
import de.sustineo.simdesk.client.protocol.*;
import de.sustineo.simdesk.client.protocol.enums.Nationality;
import de.sustineo.simdesk.client.protocol.enums.SessionPhase;
import de.sustineo.simdesk.client.protocol.enums.SessionType;
import de.sustineo.simdesk.eventbus.EventBus;
import lombok.extern.java.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public class AccBroadcastingThread extends Thread implements AccBroadcastingProtocolCallback {
    private static final int BUFFER_SIZE = 2048;
    private static final Duration SOCKET_TIMEOUT = Duration.ofSeconds(10);

    private final AccBroadcastingState accBroadcastingState;

    private final DatagramSocket socket;
    private boolean running = true;
    private boolean forceExit = false;
    private ExitState exitState = ExitState.NONE;


    private final Map<SessionType, Integer> sessionCounter = new HashMap<>();
    private SessionPhase sessionPhase = SessionPhase.NONE;
    private Instant lastEntryListRequest = Instant.now();

    public AccBroadcastingThread(AccBroadcastingState accBroadcastingState) throws SocketException {
        super("ACC connection thread");
        this.accBroadcastingState = accBroadcastingState;

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());

        this.socket = new DatagramSocket();
        this.socket.setSoTimeout((int) SOCKET_TIMEOUT.toMillis());
        this.socket.connect(accBroadcastingState.getHostAddress(), accBroadcastingState.getHostPort());
    }

    @Override
    public void run() {
        EventBus.publish(new ConnectionOpenedEvent());

        sendRegisterRequest();

        while (running) {
            try {
                DatagramPacket response = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                socket.receive(response);

                onPacketReceived(response.getData());

                AccBroadcastingProtocol.processMessage(new ByteArrayInputStream(response.getData()), this);
            } catch (SocketTimeoutException e) {
                log.warning("ACC Socket timed out");
                exitState = ExitState.TIMEOUT;
                running = false;
            } catch (PortUnreachableException e) {
                log.severe("ACC Socket is unreachable");
                exitState = ExitState.PORT_UNREACHABLE;
                running = false;
            } catch (SocketException e) {
                if (forceExit) {
                    log.info("ACC Socket was closed by user.");
                    exitState = ExitState.USER;
                } else {
                    log.severe(String.format("ACC Socket closed unexpected: %s", e.getMessage()));
                    exitState = ExitState.EXCEPTION;
                }
                running = false;
            } catch (StackOverflowError | IOException e) {
                log.severe(String.format("Error in ACC listener thread: %s", e.getMessage()));
                exitState = ExitState.EXCEPTION;
                running = false;
            }
        }

        EventBus.publish(new ConnectionClosedEvent(exitState));
    }

    public void close() {
        super.interrupt();
        forceExit = true;
        socket.close();
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && super.isAlive() && running;
    }

    /**
     * Send a register request.
     */
    public void sendRegisterRequest() {
        if (!isConnected()) {
            return;
        }

        sendRequest(AccBroadcastingProtocol.buildRegisterRequest(
                accBroadcastingState.getDisplayName(),
                accBroadcastingState.getConnectionPassword(),
                accBroadcastingState.getUpdateInterval(),
                accBroadcastingState.getCommandPassword()
        ));
    }

    /**
     * Send unregister request.
     */
    public void sendUnregisterRequest() {
        if (!isConnected()) {
            return;
        }

        sendRequest(AccBroadcastingProtocol.buildUnregisterRequest(accBroadcastingState.getConnectionId()));
    }

    /**
     * Send a request for the current entry list.
     */
    public void sendEntryListRequest() {
        if (!isConnected()) {
            return;
        }

        // Avoid sending too many entrylist requests in a short time
        Instant now = Instant.now();
        if (Duration.between(lastEntryListRequest, now).compareTo(Duration.ofSeconds(5)) > 0) {
            lastEntryListRequest = now;
            sendRequest(AccBroadcastingProtocol.buildEntryListRequest(accBroadcastingState.getConnectionId()));
        }
    }

    /**
     * Send a request for the current track data.
     */
    public void sendTrackDataRequest() {
        if (!isConnected()) {
            return;
        }

        sendRequest(AccBroadcastingProtocol.buildTrackDataRequest(accBroadcastingState.getConnectionId()));
    }

    public void sendRequest(byte[] requestBytes) {
        if (socket.isConnected()) {
            try {
                socket.send(new DatagramPacket(requestBytes, requestBytes.length));
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error sending request.", e);
            }
        }
    }

    @Override
    public void onPacketReceived(byte[] payload) {
        EventBus.publish(new PacketReceivedEvent(payload));
    }

    @Override
    public void onRegistrationResult(int connectionId, boolean success, boolean readOnly, String message) {
        if (!success) {
            log.warning("Connection refused: " + message);
            running = false;
            return;
        }

        accBroadcastingState.setConnectionId(connectionId);
        accBroadcastingState.setReadOnly(readOnly);

        sendEntryListRequest();
        sendTrackDataRequest();

        EventBus.publish(new RegistrationResultEvent(connectionId, success, readOnly, message));
    }

    @Override
    public void onRealtimeUpdate(SessionInfo sessionInfo) {
        accBroadcastingState.getSession().setRaw(sessionInfo);

        checkForDisconnects();

        if (!accBroadcastingState.getCurrentSessionId().isValid()) {
            log.info("Initialising session");
            initSessionId(sessionInfo);
            // fast-forward to correct phase
            while (sessionInfo.getPhase().getId() > sessionPhase.getId()) {
                sessionPhase = SessionPhase.getNext(sessionPhase);
                onSessionPhaseChanged(sessionPhase, sessionInfo, true);
            }
        }

        // Update the current session.
        if (accBroadcastingState.getCurrentSessionId().getIndex() != sessionInfo.getSessionIndex()) {
            // Fast-forward current session to result UI
            while (sessionPhase != SessionPhase.RESULT_UI) {
                sessionPhase = SessionPhase.getNext(sessionPhase);
                onSessionPhaseChanged(sessionPhase, sessionInfo, false);
            }
            // Move to next sessionId;
            SessionType type = sessionInfo.getSessionType();
            int sessionIndex = sessionInfo.getSessionIndex();
            int sessionNumber = sessionCounter.getOrDefault(type, -1) + 1;
            sessionCounter.put(type, sessionNumber);

            SessionId newSessionId = SessionId.of(type, sessionIndex, sessionNumber);
            onSessionChanged(newSessionId, sessionInfo, false);
            accBroadcastingState.setCurrentSessionId(newSessionId);

            sessionPhase = SessionPhase.NONE;
        }

        // Fast-forward to current phase
        while (sessionInfo.getPhase().getId() > sessionPhase.getId()) {
            sessionPhase = SessionPhase.getNext(sessionPhase);
            onSessionPhaseChanged(sessionPhase, sessionInfo, false);
        }

        EventBus.publish(new RealtimeUpdateEvent(sessionInfo));
    }

    private void checkForDisconnects() {
        long now = System.currentTimeMillis();
        accBroadcastingState.getCars().forEach(car -> {
            if (car.connected) {
                if (now - car.lastUpdate > accBroadcastingState.getUpdateInterval() * 10L) {
                    car.connected = false;

                    String name = car.getDriver().fullName();

                    log.fine("Car " + car.carNumberString()
                            + " received last update "
                            + (now - car.lastUpdate) + "ms ago.");
                    log.info("Car disconnected: " + car.carNumberString() + "\t" + name);
                    EventBus.publish(new CarDisconnectedEvent(car));
                }
            }
        });
    }

    private void initSessionId(SessionInfo sessionInfo) {
        log.fine("Initialising session: "
                + "Type=" + sessionInfo.getSessionType()
                + ", Phase=" + sessionInfo.getPhase()
                + ", Time=" + sessionInfo.getSessionEndTime()
        );
        SessionType type = sessionInfo.getSessionType();
        int sessionIndex = sessionInfo.getSessionIndex();
        int sessionNumber = sessionCounter.getOrDefault(type, -1) + 1;
        sessionCounter.put(type, sessionNumber);

        SessionId newSessionId = SessionId.of(type, sessionIndex, sessionNumber);
        onSessionChanged(newSessionId, sessionInfo, true);
        accBroadcastingState.setCurrentSessionId(newSessionId);

        sessionPhase = SessionPhase.NONE;
    }

    @Override
    public void onRealtimeCarUpdate(RealtimeInfo info) {
        accBroadcastingState.getCar(info.getCarId()).ifPresentOrElse(car -> {
            car.lastUpdate = System.currentTimeMillis();
            car.connected = true;
            car.driverIndexRealtime = info.getDriverIndex();
            car.driverCount = info.getDriverCount();
            car.gear = info.getGear();
            car.yaw = info.getYaw();
            car.pitch = info.getPitch();
            car.roll = info.getRoll();
            car.carLocation = info.getLocation();
            car.KMH = info.getKmh();
            car.position = info.getPosition();
            car.cupPosition = info.getCupPosition();
            car.trackPosition = info.getTrackPosition();
            car.splinePosition = info.getSplinePosition();
            car.lapCount = info.getLaps();
            car.delta = info.getDelta();
            car.bestLap = info.getBestSessionLap();
            car.lastLap = info.getLastLap();
            car.currentLap = info.getCurrentLap();
            EventBus.publish(new RealtimeCarUpdateEvent(info));
        }, () -> {
            //if the car doesn't exist in the state ask for a new entry list.
            log.fine("Realtime update for unknown car. Sending entry list request");
            sendEntryListRequest();
        });
    }

    @Override
    public void onEntryListUpdate(List<Integer> carIds) {
        EventBus.publish(new EntryListUpdateEvent(carIds));
    }

    @Override
    public void onTrackData(TrackInfo info) {
        accBroadcastingState.setTrackInfo(info);

        EventBus.publish(new TrackInfoEvent(info));
    }

    @Override
    public void onEntryListCarUpdate(CarInfo carInfo) {
        Car car = accBroadcastingState.getCar(carInfo.getCarId())
                .orElse(new Car());

        boolean newConnection = !car.connected;

        car.lastUpdate = System.currentTimeMillis();
        car.connected = true;
        car.id = carInfo.getCarId();
        car.carModel = carInfo.getCarModel();
        car.teamName = carInfo.getTeamName();
        car.carNumber = carInfo.getCarNumber();
        car.cupCategory = carInfo.getCupCategory();
        car.driverIndex = carInfo.getCurrentDriverIndex();
        car.nationality = Nationality.fromId(carInfo.getCarNationality());
        car.drivers = carInfo.getDrivers().stream()
                .map(driverInfo -> {
                    var driver = new Driver();
                    driver.firstName = driverInfo.getFirstName();
                    driver.lastName = driverInfo.getLastName();
                    driver.shortName = driverInfo.getShortName();
                    driver.category = driverInfo.getCategory();
                    driver.nationality = driverInfo.getDriverNationality();
                    return driver;
                })
                .collect(Collectors.toList());
        accBroadcastingState.putCar(car);

        // Fire Car connection event if the car is new.
        if (newConnection) {
            log.info("Car connected: " + car.carNumberString() + "\t" + car.getDriver().fullName());
            EventBus.publish(new CarConnectedEvent(car));
        }

        EventBus.publish(new EntryListCarUpdateEvent(carInfo));
    }

    @Override
    public void onBroadcastingEvent(BroadcastingEvent event) {
        EventBus.publish(new BroadcastingEventEvent(event));
    }

    private void onSessionChanged(SessionId newId, SessionInfo info, boolean init) {
        log.info("session changed to " + newId.getType().name() + " Index:" + newId.getIndex() + " sessionCount:" + newId.getNumber());

        EventBus.publish(new SessionChangedEvent(newId, info, init));
    }

    private void onSessionPhaseChanged(SessionPhase phase, SessionInfo info, boolean init) {
        log.info("session phase changed to " + phase.name());

        SessionInfo correctedSessionInfo = SessionInfo.builder()
                .eventIndex(info.getEventIndex())
                .sessionIndex(info.getSessionIndex())
                .sessionType(info.getSessionType())
                .phase(phase)
                .sessionTime(info.getSessionTime())
                .sessionEndTime(info.getSessionEndTime())
                .focusedCarIndex(info.getFocusedCarIndex())
                .activeCameraSet(info.getActiveCameraSet())
                .activeCamera(info.getActiveCamera())
                .currentHudPage(info.getCurrentHudPage())
                .replayPlaying(info.isReplayPlaying())
                .replaySessionTime(info.getReplaySessionTime())
                .replayRemainingTime(info.getReplayRemainingTime())
                .timeOfDay(info.getTimeOfDay())
                .ambientTemp(info.getAmbientTemp())
                .trackTemp(info.getTrackTemp())
                .cloudLevel(info.getCloudLevel())
                .rainLevel(info.getRainLevel())
                .wetness(info.getWetness())
                .bestSessionLap(info.getBestSessionLap())
                .build();

        EventBus.publish(new SessionPhaseChangedEvent(correctedSessionInfo, init));
    }

    public enum ExitState {
        NONE,
        USER,
        REFUSED,
        PORT_UNREACHABLE,
        EXCEPTION,
        TIMEOUT
    }

    private static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.log(Level.SEVERE, "Uncaught exception:", e);
        }
    }
}
