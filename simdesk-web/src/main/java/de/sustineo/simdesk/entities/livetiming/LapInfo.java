
package de.sustineo.simdesk.entities.livetiming;

import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccLapType;
import lombok.*;

import java.time.Duration;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LapInfo {
    private Duration lapTime;
    private int carId;
    private int driverIndex;
    private List<Integer> splits;
    private boolean isValid;
    private boolean isValidForBest;
    private AccLapType type;
}
