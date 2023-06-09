package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import de.sustineo.acc.leaderboard.utils.FormatUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverRanking {
    private CarGroup carGroup;
    private String trackId;
    private Long lapTimeMillis;
    private Long split1Millis;
    private Long split2Millis;
    private Long split3Millis;
    private Driver driver;
    private Integer carModelId;
    private Session session;

    public String getCarModelName() {
        return Car.getCarNameById(carModelId);
    }

    public String getTrackName() {
        return Track.getTrackNameById(trackId);
    }

    public String getLapTime() {
        return FormatUtils.formatLapTime(lapTimeMillis);
    }

    public String getSplit1() {
        return FormatUtils.formatLapTime(split1Millis);
    }

    public String getSplit2() {
        return FormatUtils.formatLapTime(split2Millis);
    }

    public String getSplit3() {
        return FormatUtils.formatLapTime(split3Millis);
    }
}