package de.sustineo.simdesk.entities.livetiming;

import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCarLocation;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCupCategory;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccNationality;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CarInfo {
    private int id;
    private long lastUpdate;
    private boolean connected;
    private AccCar car;
    private String teamName;
    private int carNumber;
    private AccCupCategory cupCategory;
    private int carNationality;
    private AccNationality nationality;
    private List<DriverInfo> drivers;
    private int currentDriverIndex;
    private int driverIndexRealtime;
    private int driverCount;
    private int gear;
    private int kmh;
    private float yaw;
    private float pitch;
    private float roll;
    private AccCarLocation accCarLocation;
    private int position;
    private int realtimePosition;
    private int cupPosition;
    private int trackPosition;
    private float splinePosition;
    private float raceDistance;
    private int lapCount;
    private int delta;
    @Builder.Default
    private LapInfo bestLap = new LapInfo();
    @Builder.Default
    private LapInfo lastLap = new LapInfo();
    @Builder.Default
    private LapInfo currentLap = new LapInfo();
    private int deltaToSessionBest;
    private int carPositionAhead;
    private int carPositionBehind;
    private int gapPositionAhead;
    private int gapPositionBehind;
    private int gapToLeader;
    private float lapsBehindLeader;
    private int carAhead;
    private int carBehind;
    private int gapAhead;
    private int gapBehind;
    private boolean isSessionBestLaptime;
    private boolean isFocused;
    private boolean isYellowFlag;
    private boolean isWhiteFlag;
    private boolean isCheckeredFlag;
    private int overtakeIndicator;
    private int maxKMH;
    private int speedTrapKMH;
    private int raceStartPosition;
    private boolean raceStartPositionAccurate;
    private int pitLaneTime;
    private int pitLaneTimeStationary;
    private int pitlaneCount;
    private boolean pitlaneCountAccurate;
    private int driverStintTime;
    private boolean driverStintTimeAccurate;
    @Builder.Default
    private RealtimeInfo realtime = new RealtimeInfo();

    public DriverInfo getDriver() {
        if (realtime != null && drivers.size() > realtime.getDriverIndex()) {
            return drivers.get(realtime.getDriverIndex());
        } else if (drivers.size() > currentDriverIndex) {
            return drivers.get(currentDriverIndex);
        } else {
            return new DriverInfo();
        }
    }
}
