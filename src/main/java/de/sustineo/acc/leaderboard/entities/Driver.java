package de.sustineo.acc.leaderboard.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {
    private String playerId;
    private String firstName;
    private String lastName;
    private String shortName;
}
