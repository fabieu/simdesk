package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import de.sustineo.acc.leaderboard.utils.FormatUtils;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DriverRanking extends Entity {
    private Integer ranking;
    private CarGroup carGroup;
    private String trackId;
    private Long lapTimeMillis;
    private Long split1Millis;
    private Long split2Millis;
    private Long split3Millis;
    private Driver driver;
    private Integer carModelId;
    private Session session;
    private Integer lapCount;

    public String getCarModelName() {
        return Car.getCarNameById(carModelId);
    }

    public String getTrackName() {
        return Track.getTrackNameById(trackId);
    }

    public String getLapTime() {
        return FormatUtils.formatLapTime(lapTimeMillis);
    }

    public String getDriverFullName() {
        return driver.getFullName();
    }

    public String getSessionDescription() {
        if (session == null) {
            return PLACEHOLDER;
        }

        return session.getDescription();
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