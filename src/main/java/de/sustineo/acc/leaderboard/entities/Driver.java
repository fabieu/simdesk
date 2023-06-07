package de.sustineo.acc.leaderboard.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Driver {
    private String firstName;
    private String lastName;
    private String shortName;
    private String playerId;
}
