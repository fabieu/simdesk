package de.sustineo.acc.leaderboard.entities.ranking;

import de.sustineo.acc.leaderboard.entities.Car;
import de.sustineo.acc.leaderboard.entities.Driver;
import de.sustineo.acc.leaderboard.entities.Session;
import de.sustineo.acc.leaderboard.entities.Track;
import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionRanking {
    private Session session;
    private Integer ranking;
    private CarGroup carGroup;
    private Integer carModelId;
    private Integer ballastKg;
    private Integer raceNumber;
    private List<Driver> drivers;
    private Long bestLapTimeMillis;
    private Long bestSplit1Millis;
    private Long bestSplit2Millis;
    private Long bestSplit3Millis;
    private Long totalTimeMillis;
    private Integer lapCount;

    public String getCarModelName() {
        return Car.getCarNameById(carModelId);
    }

    public String getTrackName() {
        return Track.getTrackNameById(session.getTrackId());
    }
}
