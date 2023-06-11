package de.sustineo.acc.leaderboard.entities;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LapCount extends Entity {
    private Boolean valid;
    private Integer lapCount;
}
