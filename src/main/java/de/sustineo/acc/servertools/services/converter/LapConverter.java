package de.sustineo.acc.servertools.services.converter;

import de.sustineo.acc.servertools.entities.FileMetadata;
import de.sustineo.acc.servertools.entities.Lap;
import de.sustineo.acc.servertools.entities.json.AccCar;
import de.sustineo.acc.servertools.entities.json.AccDriver;
import de.sustineo.acc.servertools.entities.json.AccLap;
import de.sustineo.acc.servertools.entities.json.AccSession;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Service
public class LapConverter extends BaseConverter {
    private final DriverConverter driverConverter;

    public LapConverter(DriverConverter driverConverter) {
        this.driverConverter = driverConverter;
    }

    public List<Lap> convertToLaps(Integer sessionId, AccSession accSession, FileMetadata fileMetadata) {
        return accSession.getLaps().stream()
                .map(accLap -> convertToLap(sessionId, accLap, accSession, fileMetadata))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Lap convertToLap(Integer sessionId, AccLap accLap, AccSession accSession, FileMetadata fileMetadata) {
        Optional<AccCar> accCar = accSession.getCarById(accLap.getCarId());
        if (accCar.isEmpty()) {
            log.severe(String.format("Car not found with id %s", accLap.getCarId()));
            return null;
        }

        Optional<AccDriver> accDriver = accCar.orElseThrow().getDriverByIndex(accLap.getDriverIndex());
        if (accDriver.isEmpty()) {
            log.severe(String.format("Driver not found with index %s", accLap.getDriverIndex()));
            return null;
        }

        return Lap.builder()
                .sessionId(sessionId)
                .carGroup(accCar.get().getCarGroup())
                .carModelId(accCar.get().getCarModel())
                .driver(driverConverter.convertToDriver(accDriver.get(), fileMetadata))
                .lapTimeMillis(fixBadTiming(accLap.getLapTimeMillis()))
                .split1Millis(fixBadTiming(accLap.getSplits().get(0)))
                .split2Millis(fixBadTiming(accLap.getSplits().get(1)))
                .split3Millis(fixBadTiming(accLap.getSplits().get(2)))
                .valid(accLap.getValid())
                .build();
    }
}