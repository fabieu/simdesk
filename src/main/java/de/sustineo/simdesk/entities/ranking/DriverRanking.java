package de.sustineo.simdesk.entities.ranking;

import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.Model;
import de.sustineo.simdesk.entities.Session;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DriverRanking extends Model {
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
}