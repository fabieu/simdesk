package de.sustineo.simdesk.entities.output;

import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.Lap;
import lombok.Data;

@Data
public class LapResponse {
    private Integer id;
    private Integer sessionId;
    private DriverResponse driver;
    private CarGroup carGroup;
    private Integer carModelId;
    private Long lapTimeMillis;
    private Long split1Millis;
    private Long split2Millis;
    private Long split3Millis;
    private boolean valid;

    public LapResponse(Lap lap) {
        this.id = lap.getId();
        this.sessionId = lap.getSessionId();
        this.driver = new DriverResponse(lap.getDriver());
        this.carGroup = lap.getCarGroup();
        this.carModelId = lap.getCarModelId();
        this.lapTimeMillis = lap.getLapTimeMillis();
        this.split1Millis = lap.getSplit1Millis();
        this.split2Millis = lap.getSplit2Millis();
        this.split3Millis = lap.getSplit3Millis();
        this.valid = lap.isValid();
    }
}
