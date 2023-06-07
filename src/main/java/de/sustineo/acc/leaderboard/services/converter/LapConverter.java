package de.sustineo.acc.leaderboard.services.converter;

import de.sustineo.acc.leaderboard.entities.Driver;
import de.sustineo.acc.leaderboard.entities.Lap;
import de.sustineo.acc.leaderboard.entities.json.AccCar;
import de.sustineo.acc.leaderboard.entities.json.AccDriver;
import de.sustineo.acc.leaderboard.entities.json.AccLap;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LapConverter {
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
                .lapTimeMillis(accLap.getLapTimeMillis())
                .split1Millis(accLap.getSplits().get(0))
                .split2Millis(accLap.getSplits().get(1))
                .split3Millis(accLap.getSplits().get(2))
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
