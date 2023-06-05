package de.sustineo.acc.leaderboard.entities;

import de.sustineo.acc.leaderboard.entities.enums.CarGroup;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Data
@Builder
public class Lap {
    private String id;
    private Integer sessionId;
    private CarGroup carGroup;
    private Integer carModel;
    private Driver driver;
    private Duration lapTime;
    private Duration split1;
    private Duration split2;
    private Duration split3;
    private boolean valid;
}
