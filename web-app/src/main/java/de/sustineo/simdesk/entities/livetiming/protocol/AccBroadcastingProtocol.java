package de.sustineo.simdesk.entities.livetiming.protocol;

import de.sustineo.simdesk.entities.livetiming.protocol.enums.*;
import lombok.extern.java.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Log
public class AccBroadcastingProtocol {
    private static final byte BROADCASTING_PROTOCOL_VERSION = 0x04;

    public static void processMessage(ByteArrayInputStream inputStream, AccBroadcastingProtocolCallback callback) {
        byte messageType = readByte(inputStream);
        switch (messageType) {
            case InboundMessageTypes.REGISTRATION_RESULT:
                readRegistrationResult(inputStream, callback);
                break;
            case InboundMessageTypes.REALTIME_UPDATE:
                readRealtimeUpdate(inputStream, callback);
                break;
            case InboundMessageTypes.REALTIME_CAR_UPDATE:
                readRealtimeCarUpdate(inputStream, callback);
                break;
            case InboundMessageTypes.ENTRY_LIST:
                readEntryList(inputStream, callback);
                break;
            case InboundMessageTypes.TRACK_DATA:
                readTrackData(inputStream, callback);
                break;
            case InboundMessageTypes.ENTRY_LIST_CAR:
                readEntryListCar(inputStream, callback);
                break;
            case InboundMessageTypes.BROADCASTING_EVENT:
                readBroadcastingEvent(inputStream, callback);
                break;

            default:
                log.warning("Unknown message type: " + messageType);
        }
    }

    private static void readRegistrationResult(ByteArrayInputStream inputStream, AccBroadcastingProtocolCallback callback) {
        int connectionID = readInt32(inputStream);
        boolean connectionSuccess = readByte(inputStream) > 0;
        boolean isReadonly = readByte(inputStream) == 0;
        String errorMessage = readString(inputStream);
        callback.onRegistrationResult(connectionID, connectionSuccess, isReadonly, errorMessage);
    }

    private static void readRealtimeUpdate(ByteArrayInputStream inputStream, AccBroadcastingProtocolCallback callback) {
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

        callback.onRealtimeUpdate(sessionInfo);
    }

    private static void readRealtimeCarUpdate(ByteArrayInputStream inputStream, AccBroadcastingProtocolCallback callback) {
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

        RealtimeInfo info = RealtimeInfo.builder()
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

        callback.onRealtimeCarUpdate(info);
    }

    private static void readEntryList(ByteArrayInputStream inputStream, AccBroadcastingProtocolCallback callback) {
        int connectionId = readInt32(inputStream);
        int carEntryCount = readUInt16(inputStream);

        List<Integer> cars = new LinkedList<>();
        for (int i = 0; i < carEntryCount; i++) {
            cars.add(readUInt16(inputStream));
        }

        callback.onEntryListUpdate(cars);
    }

    private static void readEntryListCar(ByteArrayInputStream inputStream, AccBroadcastingProtocolCallback callback) {
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

        callback.onEntryListCarUpdate(carInfo);
    }

    private static void readBroadcastingEvent(ByteArrayInputStream inputStream, AccBroadcastingProtocolCallback callback) {
        BroadcastingEventType type = BroadcastingEventType.fromId(readByte(inputStream));
        String message = readString(inputStream);
        int timeMs = readInt32(inputStream);
        int carId = readInt32(inputStream);

        BroadcastingEvent event = BroadcastingEvent.builder()
                .type(type)
                .message(message)
                .timeMs(timeMs)
                .carId(carId)
                .build();
        callback.onBroadcastingEvent(event);
    }

    private static void readTrackData(ByteArrayInputStream inputStream, AccBroadcastingProtocolCallback callback) {
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

        TrackInfo info = TrackInfo.builder()
                .trackName(trackName)
                .trackId(trackId)
                .trackMeters(trackMeters)
                .cameraSets(Collections.unmodifiableMap(cameraSets))
                .hudPages(Collections.unmodifiableList(hudPages))
                .build();

        callback.onTrackData(info);
    }

    public static byte[] buildRegisterRequest(String name, String password, int interval, String commandPassword) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.REGISTER_COMMAND_APPLICATION);
        message.write(BROADCASTING_PROTOCOL_VERSION);
        writeString(message, name);
        writeString(message, password);
        message.write(toByteArray(interval, 4), 0, 4);
        writeString(message, commandPassword);
        return message.toByteArray();
    }

    public static byte[] buildUnregisterRequest(int connectionID) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.UNREGISTER_COMMAND_APPLICATION);
        message.write(toByteArray(connectionID, 4), 0, 4);
        return message.toByteArray();
    }

    public static byte[] buildEntryListRequest(int connectionID) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.REQUEST_ENTRY_LIST);
        message.write(toByteArray(connectionID, 4), 0, 4);
        return message.toByteArray();
    }

    public static byte[] buildTrackDataRequest(int connectionID) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.REQUEST_TRACK_DATA);
        message.write(toByteArray(connectionID, 4), 0, 4);
        return message.toByteArray();
    }

    private static void writeString(ByteArrayOutputStream outputStream, String message) {
        outputStream.write(toByteArray(message.length(), 2), 0, 2);
        outputStream.write(message.getBytes(), 0, message.length());
    }

    private static byte[] toByteArray(int n, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) (n & 0xFF);
            n = n >> 8;
        }
        return result;
    }

    private static byte readByte(ByteArrayInputStream in) {
        return (byte) in.read();
    }

    private static int readUInt16(ByteArrayInputStream in) {
        byte[] int32 = new byte[2];
        in.read(int32, 0, 2);
        return ByteBuffer.wrap(int32).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    private static int readInt32(ByteArrayInputStream in) {
        byte[] int32 = new byte[4];
        in.read(int32, 0, 4);
        return ByteBuffer.wrap(int32).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static String readString(ByteArrayInputStream in) {
        int length = readUInt16(in);
        byte[] message = new byte[length];
        in.read(message, 0, length);
        return new String(message, StandardCharsets.UTF_8);
    }

    private static float readFloat(ByteArrayInputStream in) {
        byte[] int32 = new byte[4];
        in.read(int32, 0, 4);
        return ByteBuffer.wrap(int32).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    private static LapInfo readLap(ByteArrayInputStream inputStream) {
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

    public interface OutboundMessageTypes {
        byte REGISTER_COMMAND_APPLICATION = 0x01;
        byte UNREGISTER_COMMAND_APPLICATION = 0x09;
        byte REQUEST_ENTRY_LIST = 0x0A;
        byte REQUEST_TRACK_DATA = 0x0B;
        byte CHANGE_FOCUS = 0x32;
    }

    public interface InboundMessageTypes {
        byte REGISTRATION_RESULT = 0x01;
        byte REALTIME_UPDATE = 0x02;
        byte REALTIME_CAR_UPDATE = 0x03;
        byte ENTRY_LIST = 0x04;
        byte TRACK_DATA = 0x05;
        byte ENTRY_LIST_CAR = 0x06;
        byte BROADCASTING_EVENT = 0x07;
    }

}
