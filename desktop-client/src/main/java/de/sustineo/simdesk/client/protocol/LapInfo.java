
package de.sustineo.simdesk.client.protocol;

import de.sustineo.simdesk.client.protocol.enums.LapType;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LapInfo {
    private int lapTimeMS;
    private int carId;
    private int driverIndex;
    @Builder.Default
    private List<Integer> splits = Arrays.asList(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    private boolean isInvalid;
    private boolean isValidForBest;
    @Builder.Default
    private LapType type = LapType.ERROR;
}
