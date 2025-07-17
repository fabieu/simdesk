package de.sustineo.simdesk.services.livetiming;

import de.sustineo.simdesk.entities.livetiming.protocol.*;
import de.sustineo.simdesk.entities.livetiming.protocol.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Log
@Service
@RequiredArgsConstructor
public class LiveTimingProcessor {
    private final LiveTimingStateService liveTimingStateService;

    public void processMessage(String sessionId, String dashboardId, byte[] payload) {
        log.fine("Handle livetiming payload from dashboardId: " + dashboardId + ", data: " + new String(payload));

        ByteArrayInputStream inputStream = new ByteArrayInputStream(payload);

        byte messageType = readByte(inputStream);
        switch (messageType) {
            case InboundMessageTypes.REGISTRATION_RESULT:
                processRegistrationResult(sessionId, dashboardId, inputStream);
                break;
            case InboundMessageTypes.REALTIME_UPDATE:
                processRealtimeUpdate(sessionId, dashboardId, inputStream);
                break;
            case InboundMessageTypes.REALTIME_CAR_UPDATE:
                processRealtimeCarUpdate(sessionId, dashboardId, inputStream);
                break;
            case InboundMessageTypes.ENTRY_LIST:
                processEntryList(sessionId, dashboardId, inputStream);
                break;
            case InboundMessageTypes.TRACK_DATA:
                processTrackData(sessionId, dashboardId, inputStream);
                break;
            case InboundMessageTypes.ENTRY_LIST_CAR:
                processEntryListCar(sessionId, dashboardId, inputStream);
                break;
            case InboundMessageTypes.BROADCASTING_EVENT:
                processBroadcastingEvent(sessionId, dashboardId, inputStream);
                break;
            default:
                log.warning("Unknown message type: " + messageType);
        }
    }

    private void processRegistrationResult(String sessionId, String dashboardId, ByteArrayInputStream inputStream) {
        int connectionId = readInt32(inputStream);
        boolean connectionSuccess = readByte(inputStream) > 0;
        boolean isReadonly = readByte(inputStream) == 0;
        String errorMessage = readString(inputStream);

        liveTimingStateService.handleRegistrationResult(sessionId, dashboardId, connectionId, connectionSuccess, isReadonly, errorMessage);
    }

    private void processRealtimeUpdate(String sessionId, String dashboardId, ByteArrayInputStream inputStream) {
        int eventIndex = readUInt16(inputStream);
        int sessionIndex = readUInt16(inputStream);
        SessionType sessionType = SessionType.fromId(readByte(inputStream));
        SessionPhase phase = SessionPhase.fromId(readByte(inputStream));
        float sessionTime = readFloat(inputStream);
        float sessionEndTime = readFloat(inputStream);

        int focusedCarIndex = readInt32(inputStream);
        String activeCameraSet = readString(inputStream);
        String activeCamera = readString(inputStream);
        String currentHudPage = readString(inputStream);

        boolean isReplayPlaying = readByte(inputStream) > 0;
        float replaySessionTime = 0;
        float replayRemainingTime = 0;
        if (isReplayPlaying) {
            replaySessionTime = readFloat(inputStream);
            replayRemainingTime = readFloat(inputStream);
        }

        float timeOfDay = readFloat(inputStream);
        byte ambientTemp = readByte(inputStream);
        byte trackTemp = readByte(inputStream);
        byte cloudLevel = readByte(inputStream);
        byte rainLevel = readByte(inputStream);
        byte wetness = readByte(inputStream);

        LapInfo bestSessionLap = readLap(inputStream);

        SessionInfo sessionInfo = SessionInfo.builder()
                .eventIndex(eventIndex)
                .sessionIndex(sessionIndex)
                .sessionType(sessionType)
                .phase(phase)
                .sessionTime((int) sessionTime)
                .sessionEndTime((int) sessionEndTime)
                .focusedCarIndex(focusedCarIndex)
                .activeCameraSet(activeCameraSet)
                .activeCamera(activeCamera)
                .currentHudPage(currentHudPage)
                .replayPlaying(isReplayPlaying)
                .replaySessionTime((int) replaySessionTime)
                .replayRemainingTime((int) replayRemainingTime)
                .timeOfDay((int) timeOfDay)
                .ambientTemp(ambientTemp)
                .trackTemp(trackTemp)
                .cloudLevel(cloudLevel)
                .rainLevel(rainLevel)
                .wetness(wetness)
                .bestSessionLap(bestSessionLap)
                .build();

        liveTimingStateService.handleRealtimeUpdate(sessionId, dashboardId, sessionInfo);
    }

    private void processRealtimeCarUpdate(String sessionId, String dashboardId, ByteArrayInputStream inputStream) {
        int carId = readUInt16(inputStream);
        int driverIndex = readUInt16(inputStream);
        byte driverCount = readByte(inputStream);
        byte gear = readByte(inputStream);
        float yaw = readFloat(inputStream);  // falsely documented as posX
        float pitch = readFloat(inputStream);// falsely documented as posY
        float roll = readFloat(inputStream);  // falsely documented as yaw
        CarLocation location = CarLocation.fromId(readByte(inputStream));
        int kmh = readUInt16(inputStream);
        int position = readUInt16(inputStream);
        int cupPosition = readUInt16(inputStream);
        int trackPosition = readUInt16(inputStream);
        float splinePosition = readFloat(inputStream);
        int laps = readUInt16(inputStream);
        int delta = readInt32(inputStream);
        LapInfo bestSessionLap = readLap(inputStream);
        LapInfo lasLap = readLap(inputStream);
        LapInfo currentLap = readLap(inputStream);

        RealtimeInfo realtimeInfo = RealtimeInfo.builder()
                .carId(carId)
                .driverIndex(driverIndex)
                .driverCount(driverCount)
                .gear(gear)
                .yaw(yaw)
                .pitch(pitch)
                .roll(roll)
                .location(location)
                .kmh(kmh)
                .position(position)
                .cupPosition(cupPosition)
                .trackPosition(trackPosition)
                .splinePosition(splinePosition)
                .laps(laps)
                .delta(delta)
                .bestSessionLap(bestSessionLap)
                .lastLap(lasLap)
                .currentLap(currentLap)
                .build();

        liveTimingStateService.handleRealtimeCarUpdate(sessionId, dashboardId, realtimeInfo);
    }

    private void processEntryList(String sessionId, String dashboardId, ByteArrayInputStream inputStream) {
        int connectionId = readInt32(inputStream);
        int carEntryCount = readUInt16(inputStream);

        List<Integer> cars = new LinkedList<>();
        for (int i = 0; i < carEntryCount; i++) {
            cars.add(readUInt16(inputStream));
        }

        liveTimingStateService.handleEntryListUpdate(sessionId, dashboardId, cars);
    }

    private void processEntryListCar(String sessionId, String dashboardId, ByteArrayInputStream inputStream) {
        int carId = readUInt16(inputStream);
        byte carModelType = readByte(inputStream);
        String teamName = readString(inputStream);
        int carNumber = readInt32(inputStream);
        byte cupCategory = readByte(inputStream);
        byte currentDriverIndex = readByte(inputStream);
        int carNationality = readUInt16(inputStream);

        int _driverCount = readByte(inputStream);
        List<DriverInfo> drivers = new LinkedList<>();
        for (int i = 0; i < _driverCount; i++) {
            String firstName = readString(inputStream);
            String lastName = readString(inputStream);
            String shortName = readString(inputStream);
            DriverCategory category = DriverCategory.fromId(readByte(inputStream));
            Nationality driverNationality = Nationality.fromId(readUInt16(inputStream));

            DriverInfo driverInfo = DriverInfo.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .shortName(shortName)
                    .category(category)
                    .driverNationality(driverNationality)
                    .build();

            drivers.add(driverInfo);
        }

        CarInfo carInfo = CarInfo.builder()
                .carId(carId)
                .carModel(CarModel.fromType(carModelType))
                .teamName(teamName)
                .carNumber(carNumber)
                .cupCategory(cupCategory)
                .currentDriverIndex(currentDriverIndex)
                .carNationality(carNationality)
                .drivers(drivers)
                .realtime(new RealtimeInfo())
                .build();

        liveTimingStateService.handleEntrylistCarUpdate(sessionId, dashboardId, carInfo);
    }

    private void processBroadcastingEvent(String sessionId, String dashboardId, ByteArrayInputStream inputStream) {
        BroadcastingEventType type = BroadcastingEventType.fromId(readByte(inputStream));
        String message = readString(inputStream);
        int timeMs = readInt32(inputStream);
        int carId = readInt32(inputStream);

        BroadcastingEvent broadcastingEvent = BroadcastingEvent.builder()
                .type(type)
                .message(message)
                .timeMs(timeMs)
                .carId(carId)
                .build();

        liveTimingStateService.handleBroadcastingEvent(sessionId, dashboardId, broadcastingEvent);
    }

    private void processTrackData(String sessionId, String dashboardId, ByteArrayInputStream inputStream) {
        int connectionID = readInt32(inputStream);
        String trackName = readString(inputStream);
        int trackId = readInt32(inputStream);
        int trackMeters = readInt32(inputStream);

        Map<String, List<String>> cameraSets = new HashMap<>();
        byte cameraSetCount = readByte(inputStream);
        for (int camSet = 0; camSet < cameraSetCount; camSet++) {
            String camSetName = readString(inputStream);
            cameraSets.put(camSetName, new LinkedList<>());

            byte cameraCount = readByte(inputStream);
            for (int cam = 0; cam < cameraCount; cam++) {

                String camName = readString(inputStream);
                cameraSets.get(camSetName).add(camName);
            }
        }

        List<String> hudPages = new LinkedList<>();
        byte hudPagesCount = readByte(inputStream);
        for (int i = 0; i < hudPagesCount; i++) {
            hudPages.add(readString(inputStream));
        }

        TrackInfo trackInfo = TrackInfo.builder()
                .trackName(trackName)
                .trackId(trackId)
                .trackMeters(trackMeters)
                .cameraSets(Collections.unmodifiableMap(cameraSets))
                .hudPages(Collections.unmodifiableList(hudPages))
                .build();

        liveTimingStateService.handleTrackData(sessionId, dashboardId, trackInfo);
    }

    private LapInfo readLap(ByteArrayInputStream inputStream) {
        int lapTimeMS = readInt32(inputStream);
        int carId = readUInt16(inputStream);
        int driverIndex = readUInt16(inputStream);

        int splitCount = readByte(inputStream);
        List<Integer> splits = new LinkedList<>();
        for (int i = 0; i < splitCount; i++) {
            splits.add(readInt32(inputStream));
        }
        for (int i = splitCount; i < 3; i++) {
            splits.add(0);
        }

        boolean isInvalid = readByte(inputStream) > 0;
        boolean isValidForBest = readByte(inputStream) > 0;

        boolean isOutLap = readByte(inputStream) > 0;
        boolean isInLap = readByte(inputStream) > 0;
        LapType type = LapType.REGULAR;
        if (isOutLap) {
            type = LapType.OUTLAP;
        } else if (isInLap) {
            type = LapType.INLAP;
        }

        return LapInfo.builder()
                .lapTimeMS(lapTimeMS)
                .carId(carId)
                .driverIndex(driverIndex)
                .splits(splits)
                .isInvalid(isInvalid)
                .isValidForBest(isValidForBest)
                .type(type)
                .build();
    }


    private byte readByte(ByteArrayInputStream in) {
        return (byte) in.read();
    }

    private int readUInt16(ByteArrayInputStream in) {
        byte[] int32 = new byte[2];
        in.read(int32, 0, 2);
        return ByteBuffer.wrap(int32).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    private int readInt32(ByteArrayInputStream in) {
        byte[] int32 = new byte[4];
        in.read(int32, 0, 4);
        return ByteBuffer.wrap(int32).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private String readString(ByteArrayInputStream in) {
        int length = readUInt16(in);
        byte[] message = new byte[length];
        in.read(message, 0, length);
        return new String(message, StandardCharsets.UTF_8);
    }

    private float readFloat(ByteArrayInputStream in) {
        byte[] int32 = new byte[4];
        in.read(int32, 0, 4);
        return ByteBuffer.wrap(int32).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    private interface InboundMessageTypes {
        byte REGISTRATION_RESULT = 0x01;
        byte REALTIME_UPDATE = 0x02;
        byte REALTIME_CAR_UPDATE = 0x03;
        byte ENTRY_LIST = 0x04;
        byte TRACK_DATA = 0x05;
        byte ENTRY_LIST_CAR = 0x06;
        byte BROADCASTING_EVENT = 0x07;
    }
}
