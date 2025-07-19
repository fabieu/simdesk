
package de.sustineo.simdesk.entities.livetiming;

import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccBroadcastingEventType;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BroadcastingInfo {
    private AccBroadcastingEventType type;
    private String message;
    private int timeMs;
    private int carId;
}
