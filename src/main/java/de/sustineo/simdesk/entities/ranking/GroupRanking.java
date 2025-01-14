package de.sustineo.simdesk.entities.ranking;

import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.Driver;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupRanking {
    private CarGroup carGroup;
    private String trackId;
    private Long lapTimeMillis;
    private Driver driver;
    private Integer carModelId;
}
