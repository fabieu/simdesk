package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DurationFormatUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ranking {
    private CarGroup carGroup;
    private String trackId;
    private Long lapTimeMillis;
    private String driverId;
    private Integer carModel;

    public String getLapTime() {
        return DurationFormatUtils.formatDuration(lapTimeMillis, "mm:ss.SSS", true);
    }

    public String getCarModelName() {
        return Car.getCarNameById(carModel);
    }

    public String getTrackName() {
        return Track.getTrackNameById(trackId);
    }
}
