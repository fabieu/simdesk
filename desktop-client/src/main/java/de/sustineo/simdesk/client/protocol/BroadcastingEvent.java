
package de.sustineo.simdesk.client.protocol;

import de.sustineo.simdesk.client.protocol.enums.BroadcastingEventType;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BroadcastingEvent {
    private BroadcastingEventType type;
    @Builder.Default
    private String message = "";
    private int timeMs;
    private int carId;
}
