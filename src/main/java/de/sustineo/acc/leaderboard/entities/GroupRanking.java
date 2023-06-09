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
public class GroupRanking {
    private CarGroup carGroup;
    private String trackId;
    private Long lapTimeMillis;
    private Driver driver;
    private Integer carModelId;

    public String getLapTime() {
        return FormatUtils.formatLapTime(lapTimeMillis);
    }

    public String getCarModelName() {
        return Car.getCarNameById(carModelId);
    }

    public String getTrackName() {
        return Track.getTrackNameById(trackId);
    }
}
