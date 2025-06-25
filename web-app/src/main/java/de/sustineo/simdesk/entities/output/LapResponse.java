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
    private Long sector1Millis;
    private Long sector2Millis;
    private Long sector3Millis;
    private boolean valid;

    public LapResponse(Lap lap) {
        this.id = lap.getId();
        this.sessionId = lap.getSessionId();
        this.driver = new DriverResponse(lap.getDriver());
        this.carGroup = lap.getCarGroup();
        this.carModelId = lap.getCarModelId();
        this.lapTimeMillis = lap.getLapTimeMillis();
        this.sector1Millis = lap.getSector1Millis();
        this.sector2Millis = lap.getSector2Millis();
        this.sector3Millis = lap.getSector3Millis();
        this.valid = lap.isValid();
    }
}
