package de.sustineo.simdesk.entities.ranking;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverBestSectors {
    String driverId;
    private Long bestSector1Millis;
    private Long bestSector2Millis;
    private Long bestSector3Millis;

    public Long getTheoreticalBestLapMillis() {
        if (bestSector1Millis == null || bestSector2Millis == null || bestSector3Millis == null) {
            return null;
        }

        return bestSector1Millis + bestSector2Millis + bestSector3Millis;
    }
}
