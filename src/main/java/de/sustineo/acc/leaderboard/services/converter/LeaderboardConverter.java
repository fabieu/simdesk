package de.sustineo.acc.leaderboard.services.converter;

import de.sustineo.acc.leaderboard.entities.FileMetadata;
import de.sustineo.acc.leaderboard.entities.LeaderboardLine;
import de.sustineo.acc.leaderboard.entities.json.AccLeaderboardLine;
import de.sustineo.acc.leaderboard.entities.json.AccSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardConverter {
    private final DriverConverter driverConverter;

    public LeaderboardConverter(DriverConverter driverConverter) {
        this.driverConverter = driverConverter;
    }

    public List<LeaderboardLine> convertToLeaderboardLines(Integer sessionId, AccSession accSession, FileMetadata fileMetadata) {
        List<LeaderboardLine> leaderboardLines = new ArrayList<>();
        List<AccLeaderboardLine> accLeaderboardLines = accSession.getSessionResult().getLeaderboardLines();

        for (int i = 0; i < accLeaderboardLines.size(); i++) {
            LeaderboardLine leaderboardLine = convertToLeaderBoardLine(i, sessionId, accLeaderboardLines.get(i), fileMetadata);
            leaderboardLines.add(leaderboardLine);
        }

        return leaderboardLines;
    }

    private LeaderboardLine convertToLeaderBoardLine(Integer index, Integer sessionId, AccLeaderboardLine accLeaderboardLine, FileMetadata fileMetadata) {
        return LeaderboardLine.builder()
                .sessionId(sessionId)
                .ranking(index + 1)
                .cupCategory(accLeaderboardLine.getCar().getCupCategory())
                .carId(accLeaderboardLine.getCar().getCarId())
                .carGroup(accLeaderboardLine.getCar().getCarGroup())
                .carModelId(accLeaderboardLine.getCar().getCarModel())
                .raceNumber(accLeaderboardLine.getCar().getRaceNumber())
                .drivers(accLeaderboardLine.getCar().getDrivers().stream().map(driver -> driverConverter.convertToLeaderboardDriver(driver, fileMetadata, accLeaderboardLine)).toList())
                .bestLapTimeMillis(accLeaderboardLine.getTiming().getBestLap())
                .bestSplit1Millis(accLeaderboardLine.getTiming().getBestSplits().get(0))
                .bestSplit2Millis(accLeaderboardLine.getTiming().getBestSplits().get(1))
                .bestSplit3Millis(accLeaderboardLine.getTiming().getBestSplits().get(2))
                .totalTimeMillis(accLeaderboardLine.getTiming().getTotalTime())
                .lapCount(accLeaderboardLine.getTiming().getLapCount())
                .build();
    }
}
