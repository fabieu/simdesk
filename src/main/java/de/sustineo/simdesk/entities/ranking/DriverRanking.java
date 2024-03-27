package de.sustineo.simdesk.entities.ranking;

import de.sustineo.simdesk.entities.*;
import de.sustineo.simdesk.entities.enums.CarGroup;
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

    public String getCarModelName() {
        return Car.getCarNameById(carModelId);
    }

    public String getTrackName() {
        return Track.getTrackNameById(trackId);
    }
}