package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import de.sustineo.acc.leaderboard.utils.FormatUtils;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GroupRanking extends Entity {
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
