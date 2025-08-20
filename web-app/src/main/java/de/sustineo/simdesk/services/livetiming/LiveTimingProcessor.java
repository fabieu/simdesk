package de.sustineo.simdesk.services.livetiming;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.*;
import de.sustineo.simdesk.entities.livetiming.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Profile(ProfileManager.PROFILE_LIVE_TIMING)
@Log
@Service
@RequiredArgsConstructor
public class LiveTimingProcessor {
    private final LiveTimingStateService liveTimingStateService;

    public void processMessage(String sessionId, String dashboardId, byte[] payload) {
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
        boolean readOnly = readByte(inputStream) == 0;
        String errorMessage = readString(inputStream);

        liveTimingStateService.handleRegistrationResult(sessionId, dashboardId, connectionId, connectionSuccess, readOnly, errorMessage);
    }

    private void processRealtimeUpdate(String sessionId, String dashboardId, ByteArrayInputStream inputStream) {
        int eventIndex = readUInt16(inputStream);
        int sessionIndex = readUInt16(inputStream);
        AccSessionType sessionType = AccSessionType.fromId(readByte(inputStream));
        AccSessionPhase phase = AccSessionPhase.getById(readByte(inputStream));
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
        AccCarLocation location = AccCarLocation.getById(readByte(inputStream));
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

        List<Integer> cars = new ArrayList<>(carEntryCount);
        for (int i = 0; i < carEntryCount; i++) {
            cars.add(readUInt16(inputStream));
        }

        liveTimingStateService.handleEntryListUpdate(sessionId, dashboardId, cars);
    }

    private void processEntryListCar(String sessionId, String dashboardId, ByteArrayInputStream inputStream) {
        int carId = readUInt16(inputStream);
        byte carModelId = readByte(inputStream);
        String teamName = readString(inputStream);
        int carNumber = readInt32(inputStream);
        byte cupCategory = readByte(inputStream);
        byte currentDriverIndex = readByte(inputStream);
        int carNationality = readUInt16(inputStream);

        int driverCount = readByte(inputStream);
        List<DriverInfo> drivers = new ArrayList<>(driverCount);
        for (int i = 0; i < driverCount; i++) {
            String firstName = readString(inputStream);
            String lastName = readString(inputStream);
            String shortName = readString(inputStream);
            AccDriverCategory category = AccDriverCategory.getById(readByte(inputStream));
            AccNationality driverNationality = AccNationality.getById(readUInt16(inputStream));

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
                .id(carId)
                .car(AccCar.getCarById(carModelId))
                .teamName(teamName)
                .carNumber(carNumber)
                .cupCategory(AccCupCategory.getById(cupCategory))
                .currentDriverIndex(currentDriverIndex)
                .carNationality(carNationality)
                .drivers(drivers)
                .build();

        liveTimingStateService.handleEntrylistCarUpdate(sessionId, dashboardId, carInfo);
    }

    private void processBroadcastingEvent(String sessionId, String dashboardId, ByteArrayInputStream inputStream) {
        AccBroadcastingEventType type = AccBroadcastingEventType.fromId(readByte(inputStream));
        String message = readString(inputStream);
        int timeMs = readInt32(inputStream);
        int carId = readInt32(inputStream);

        BroadcastingInfo broadcastingInfo = BroadcastingInfo.builder()
                .type(type)
                .message(message)
                .timeMs(timeMs)
                .carId(carId)
                .build();

        liveTimingStateService.handleBroadcastingEvent(sessionId, dashboardId, broadcastingInfo);
    }

    private void processTrackData(String sessionId, String dashboardId, ByteArrayInputStream inputStream) {
        int connectionID = readInt32(inputStream);
        String trackName = readString(inputStream);
        int trackId = readInt32(inputStream);
        int trackMeters = readInt32(inputStream);

        Map<String, List<String>> cameraSets = new HashMap<>();
        byte cameraSetCount = readByte(inputStream);
        for (int i = 0; i < cameraSetCount; i++) {
            String cameraSetName = readString(inputStream);

            byte cameraCount = readByte(inputStream);
            List<String> cameraNames = new ArrayList<>(cameraCount);
            for (int j = 0; j < cameraCount; j++) {
                cameraNames.add(readString(inputStream));
            }

            cameraSets.put(cameraSetName, cameraNames);
        }

        byte hudPagesCount = readByte(inputStream);
        List<String> hudPages = new ArrayList<>(hudPagesCount);
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
        int lapTimeMillis = readInt32(inputStream);
        int carId = readUInt16(inputStream);
        int driverIndex = readUInt16(inputStream);

        int splitCount = readByte(inputStream);
        List<Integer> splits = new ArrayList<>(3);
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
        AccLapType type = AccLapType.REGULAR;
        if (isOutLap) {
            type = AccLapType.OUTLAP;
        } else if (isInLap) {
            type = AccLapType.INLAP;
        }

        return LapInfo.builder()
                .lapTime(Duration.ofMillis(lapTimeMillis))
                .carId(carId)
                .driverIndex(driverIndex)
                .splits(splits)
                .isValid(!isInvalid)
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
