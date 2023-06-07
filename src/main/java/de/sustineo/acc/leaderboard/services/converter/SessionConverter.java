package de.sustineo.acc.leaderboard.services.converter;

import de.sustineo.acc.leaderboard.entities.Driver;
import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.Lap;
import de.sustineo.acc.leaderboard.entities.Session;
import de.sustineo.acc.leaderboard.entities.json.AccCar;
import de.sustineo.acc.leaderboard.entities.json.AccDriver;
import de.sustineo.acc.leaderboard.entities.json.AccLap;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SessionConverter {
    public Session convertToSession(AccSession accSession, FileMetadata fileMetadata) {
        return Session.builder()
                .sessionType(accSession.getSessionType())
                .raceWeekendIndex(accSession.getRaceWeekendIndex())
                .serverName(accSession.getServerName())
                .trackName(accSession.getTrackName())
                .wetSession(accSession.getSessionResult().getIsWetSession())
                .driverCount(accSession.getSessionResult().getLeaderboardLines().size())
                .fileChecksum(fileMetadata.getChecksum())
                .fileName(fileMetadata.getName())
                .fileDirectory(fileMetadata.getDirectory().toString())
                .build();
    }

    public List<Lap> convertToLaps(Integer sessionId, AccSession accSession) {
        return accSession.getLaps().stream()
                .map(accLap -> convertToLap(sessionId, accLap, accSession))
                .collect(Collectors.toList());
    }

    private Lap convertToLap(Integer sessionId, AccLap accLap, AccSession accSession) {
        Optional<AccCar> accCar = accSession.getCarById(accLap.getCarId());
        if (accCar.isEmpty()) {
            throw new RuntimeException(String.format("Car not found with id %s", accLap.getCarId()));
        }

        Optional<AccDriver> accDriver = accCar.orElseThrow().getDriverByIndex(accLap.getDriverIndex());
        if (accDriver.isEmpty()) {
            throw new RuntimeException(String.format("Driver not found with index %s", accLap.getDriverIndex()));
        }

        return Lap.builder()
                .sessionId(sessionId)
                .carGroup(accCar.get().getCarGroup())
                .carModel(accCar.get().getCarModel())
                .driver(convertToDriver(accDriver.get()))
                .lapTime(Duration.ofMillis(accLap.getLapTimeMillis()))
                .split1(Duration.ofMillis(accLap.getSplits().get(0)))
                .split2(Duration.ofMillis(accLap.getSplits().get(1)))
                .split3(Duration.ofMillis(accLap.getSplits().get(2)))
                .valid(accLap.getValid())
                .build();
    }

    private Driver convertToDriver(AccDriver accDriver) {
        return Driver.builder()
                .firstName(accDriver.getFirstName())
                .lastName(accDriver.getLastName())
                .shortName(accDriver.getShortName())
                .playerId(accDriver.getPlayerId())
                .build();
    }
}
