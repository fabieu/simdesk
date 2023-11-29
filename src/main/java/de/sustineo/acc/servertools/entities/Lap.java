package de.sustineo.acc.servertools.entities;

import de.sustineo.acc.servertools.entities.enums.CarGroup;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Lap extends Entity {
    private String id;
    private Integer sessionId;
    private Driver driver;
    private CarGroup carGroup;
    private Integer carModelId;
    private Long lapTimeMillis;
    private Long split1Millis;
    private Long split2Millis;
    private Long split3Millis;
    private boolean valid;

    public String getCarModelName() {
        return Car.getCarNameById(carModelId);
    }
}
