package de.sustineo.acc.servertools.entities.ranking;

import de.sustineo.acc.servertools.entities.Car;
import de.sustineo.acc.servertools.entities.Driver;
import de.sustineo.acc.servertools.entities.Entity;
import de.sustineo.acc.servertools.entities.Track;
import de.sustineo.acc.servertools.entities.enums.CarGroup;
import de.sustineo.acc.servertools.utils.FormatUtils;
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
