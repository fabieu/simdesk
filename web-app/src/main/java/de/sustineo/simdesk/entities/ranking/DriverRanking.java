package de.sustineo.simdesk.entities.ranking;

import de.sustineo.simdesk.entities.Driver;
import de.sustineo.simdesk.entities.Session;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverRanking {
    private Integer carModelId;
    private Integer ranking;
    private Long lapTimeMillis;
    private Long sector1Millis;
    private Long sector2Millis;
    private Long sector3Millis;
    private Driver driver;
    private DriverBestSectors bestSectors;
    private Session session;
}