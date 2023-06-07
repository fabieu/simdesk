package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Lap {
    private String id;
    private Integer sessionId;
    private CarGroup carGroup;
    private Integer carModel;
    private Driver driver;
    private Long lapTimeMillis;
    private Long split1Millis;
    private Long split2Millis;
    private Long split3Millis;
    private boolean valid;
}
