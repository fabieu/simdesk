package de.sustineo.simdesk.services.converter;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.FileMetadata;
import de.sustineo.simdesk.entities.LeaderboardLine;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.json.kunos.acc.AccLeaderboardLine;
import de.sustineo.simdesk.entities.json.kunos.acc.AccSession;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Service
public class LeaderboardConverter extends BaseConverter {
    private final DriverConverter driverConverter;

    public LeaderboardConverter(DriverConverter driverConverter) {
        this.driverConverter = driverConverter;
    }

    public List<LeaderboardLine> convertToLeaderboardLines(Session session, AccSession accSession, FileMetadata fileMetadata) {
        List<LeaderboardLine> leaderboardLines = new ArrayList<>();
        List<AccLeaderboardLine> accLeaderboardLines = accSession.getSessionResult().getLeaderboardLines();

        for (int i = 0; i < accLeaderboardLines.size(); i++) {
            LeaderboardLine leaderboardLine = convertToLeaderboardLine(i, session, accLeaderboardLines.get(i), fileMetadata);
            leaderboardLines.add(leaderboardLine);
        }

        return leaderboardLines;
    }

    private LeaderboardLine convertToLeaderboardLine(Integer index, Session session, AccLeaderboardLine accLeaderboardLine, FileMetadata fileMetadata) {
        return LeaderboardLine.builder()
                .session(session)
                .ranking(index + 1)
                .cupCategory(accLeaderboardLine.getCar().getCupCategory())
                .carId(accLeaderboardLine.getCar().getCarId())
                .carModelId(accLeaderboardLine.getCar().getCarModel())
                .ballastKg(accLeaderboardLine.getCar().getBallastKg())
                .raceNumber(accLeaderboardLine.getCar().getRaceNumber())
                .drivers(accLeaderboardLine.getCar().getDrivers().stream().map(driver -> driverConverter.convertToLeaderboardDriver(driver, fileMetadata, accLeaderboardLine)).toList())
                .bestLapTimeMillis(fixBadTiming(accLeaderboardLine.getTiming().getBestLap()))
                .bestSplit1Millis(fixBadTiming(accLeaderboardLine.getTiming().getBestSplits().get(0)))
                .bestSplit2Millis(fixBadTiming(accLeaderboardLine.getTiming().getBestSplits().get(1)))
                .bestSplit3Millis(fixBadTiming(accLeaderboardLine.getTiming().getBestSplits().get(2)))
                .totalTimeMillis(fixBadTiming(accLeaderboardLine.getTiming().getTotalTime()))
                .lapCount(accLeaderboardLine.getTiming().getLapCount())
                .build();
    }
}
