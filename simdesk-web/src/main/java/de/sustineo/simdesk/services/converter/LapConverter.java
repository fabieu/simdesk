package de.sustineo.simdesk.services.converter;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.json.kunos.acc.AccDriver;
import de.sustineo.simdesk.entities.json.kunos.acc.AccLap;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import de.sustineo.simdesk.entities.json.kunos.acc.AccTeam;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Profile(SpringProfile.LEADERBOARD)
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
        Optional<AccTeam> accTeam = accSession.getTeamById(accLap.getTeamId());
        if (accTeam.isEmpty()) {
            log.severe(String.format("Team not found with id %s", accLap.getTeamId()));
            return null;
        }

        Optional<AccDriver> accDriver = accTeam.orElseThrow().getDriverByIndex(accLap.getDriverIndex());
        if (accDriver.isEmpty()) {
            log.severe(String.format("Driver not found with index %s", accLap.getDriverIndex()));
            return null;
        }

        Driver driver = driverConverter.convertToDriver(accDriver.orElseThrow(), fileMetadata);

        return Lap.builder()
                .sessionId(session.getId())
                .carGroup(AccCar.getGroupById(accTeam.get().getCarModelId()))
                .carModelId(accTeam.get().getCarModelId())
                .driver(driver)
                .lapTimeMillis(fixBadTiming(accLap.getLapTimeMillis()))
                .sector1Millis(fixBadTiming(accLap.getSplits().get(0)))
                .sector2Millis(fixBadTiming(accLap.getSplits().get(1)))
                .sector3Millis(fixBadTiming(accLap.getSplits().get(2)))
                .valid(accLap.getValid())
                .build();
    }
}
