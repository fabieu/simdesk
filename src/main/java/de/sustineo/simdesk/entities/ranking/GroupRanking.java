package de.sustineo.simdesk.entities.ranking;

import de.sustineo.simdesk.entities.*;
import de.sustineo.simdesk.utils.FormatUtils;
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
        return Track.getTrackNameByAccId(trackId);
    }
}
