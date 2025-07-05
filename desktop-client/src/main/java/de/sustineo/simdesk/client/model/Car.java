package de.sustineo.simdesk.client.model;

import de.sustineo.simdesk.client.protocol.LapInfo;
import de.sustineo.simdesk.client.protocol.enums.CarLocation;
import de.sustineo.simdesk.client.protocol.enums.CarModel;
import de.sustineo.simdesk.client.protocol.enums.Nationality;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static de.sustineo.simdesk.client.protocol.enums.CarLocation.PIT_LANE;

@NoArgsConstructor
public class Car {
    public int id;
    public long lastUpdate = 0;
    public boolean connected = false;
    public CarModel carModel = CarModel.ERROR;
    public String teamName = "";
    public int carNumber;
    public int cupCategory;
    public int driverIndex;
    public Nationality nationality = Nationality.ANY;
    public List<Driver> drivers = new ArrayList<>();
    public int driverIndexRealtime;
    public int driverCount;
    public int gear;
    public int KMH;
    public float yaw;
    public float pitch;
    public float roll;
    public CarLocation carLocation = CarLocation.NONE;
    public int position;
    public int realtimePosition;
    public int cupPosition;
    public int trackPosition;
    public float splinePosition;
    public float raceDistance;
    public int lapCount;
    public int delta;
    public LapInfo bestLap = new LapInfo();
    public LapInfo lastLap = new LapInfo();
    public LapInfo currentLap = new LapInfo();
    public int deltaToSessionBest;
    public int carPositionAhead = 0;
    public int carPositionBehind = 0;
    public int gapPositionAhead = Integer.MAX_VALUE;
    public int gapPositionBehind = Integer.MAX_VALUE;
    public int gapToLeader;
    public float lapsBehindLeader;
    public int carAhead = 0;
    public int carBehind = 0;
    public int gapAhead = Integer.MAX_VALUE;
    public int gapBehind = Integer.MAX_VALUE;
    public boolean isSessionBestLaptime;
    public boolean isFocused = false;
    public boolean isYellowFlag = false;
    public boolean isWhiteFlag = false;
    public boolean isCheckeredFlag = false;
    public int overtakeIndicator;
    public int maxKMH;
    public int speedTrapKMH;
    public int raceStartPosition;
    public boolean raceStartPositionAccurate = false;
    public int pitLaneTime;
    public int pitLaneTimeStationary;
    public int pitlaneCount;
    public boolean pitlaneCountAccurate;
    public int driverStintTime;
    public boolean driverStintTimeAccurate;

    public String carNumberString() {
        return String.format("#%-3d", carNumber);
    }

    public int predictedLapTime() {
        return bestLap.getLapTimeMS() + delta;
    }

    public boolean isInPit() {
        return carLocation == PIT_LANE;
    }

    public Driver getDriver() {
        if (drivers.size() > driverIndexRealtime) {
            return drivers.get(driverIndexRealtime);
        } else if (drivers.size() > driverIndex) {
            return drivers.get(driverIndex);
        } else {
            return new Driver();
        }
    }

    public synchronized Car copy() {
        Car car = new Car();
        car.id = id;
        car.lastUpdate = lastUpdate;
        car.connected = connected;
        car.carModel = carModel;
        car.teamName = teamName;
        car.carNumber = carNumber;
        car.cupCategory = cupCategory;
        car.driverIndex = driverIndex;
        car.nationality = nationality;
        car.drivers = new ArrayList<>(drivers);
        car.driverIndexRealtime = driverIndexRealtime;
        car.driverCount = driverCount;
        car.gear = gear;
        car.KMH = KMH;
        car.yaw = yaw;
        car.pitch = pitch;
        car.roll = roll;
        car.carLocation = carLocation;
        car.position = position;
        car.realtimePosition = realtimePosition;
        car.cupPosition = cupPosition;
        car.trackPosition = trackPosition;
        car.splinePosition = splinePosition;
        car.raceDistance = raceDistance;
        car.lapCount = lapCount;
        car.delta = delta;
        car.bestLap = bestLap;
        car.lastLap = lastLap;
        car.currentLap = currentLap;
        car.deltaToSessionBest = deltaToSessionBest;
        car.carPositionAhead = carPositionAhead;
        car.carPositionBehind = carPositionBehind;
        car.gapPositionAhead = gapPositionAhead;
        car.gapPositionBehind = gapPositionBehind;
        car.gapToLeader = gapToLeader;
        car.lapsBehindLeader = lapsBehindLeader;
        car.carAhead = carAhead;
        car.carBehind = carBehind;
        car.gapAhead = gapAhead;
        car.gapBehind = gapBehind;
        car.isSessionBestLaptime = isSessionBestLaptime;
        car.isFocused = isFocused;
        car.isYellowFlag = isYellowFlag;
        car.isWhiteFlag = isWhiteFlag;
        car.isCheckeredFlag = isCheckeredFlag;
        car.overtakeIndicator = overtakeIndicator;
        car.maxKMH = maxKMH;
        car.speedTrapKMH = speedTrapKMH;
        car.raceStartPosition = raceStartPosition;
        car.raceStartPositionAccurate = raceStartPositionAccurate;
        car.pitLaneTime = pitLaneTime;
        car.pitLaneTimeStationary = pitLaneTimeStationary;
        car.pitlaneCount = pitlaneCount;
        car.pitlaneCountAccurate = pitlaneCountAccurate;
        car.driverStintTime = driverStintTime;
        car.driverStintTimeAccurate = driverStintTimeAccurate;
        return car;
    }
}
