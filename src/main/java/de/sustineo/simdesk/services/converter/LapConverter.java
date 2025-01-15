package de.sustineo.simdesk.services.converter;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.*;
import de.sustineo.simdesk.entities.json.kunos.acc.AccCar;
import de.sustineo.simdesk.entities.json.kunos.acc.AccDriver;
import de.sustineo.simdesk.entities.json.kunos.acc.AccLap;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Log
@Service
public class LapConverter extends BaseConverter {
    private final DriverConverter driverConverter;

    public LapConverter(DriverConverter driverConverter) {
        this.driverConverter = driverConverter;
    }

    public List<Lap> convertToLaps(Session session, AccSession accSession, FileMetadata fileMetadata) {
        return accSession.getLaps().stream()
                .map(accLap -> convertToLap(session, accLap, accSession, fileMetadata))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Lap convertToLap(Session session, AccLap accLap, AccSession accSession, FileMetadata fileMetadata) {
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

        Driver driver = driverConverter.convertToDriver(accDriver.orElseThrow(), fileMetadata);

        return Lap.builder()
                .sessionId(session.getId())
                .carGroup(Car.getGroupById(accCar.get().getCarModel()))
                .carModelId(accCar.get().getCarModel())
                .driver(driver)
                .lapTimeMillis(fixBadTiming(accLap.getLapTimeMillis()))
                .split1Millis(fixBadTiming(accLap.getSplits().get(0)))
                .split2Millis(fixBadTiming(accLap.getSplits().get(1)))
                .split3Millis(fixBadTiming(accLap.getSplits().get(2)))
                .valid(accLap.getValid())
                .build();
    }
}
