
package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.protocol.BroadcastingEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class BroadcastingEventEvent extends Event {
    private final BroadcastingEvent event;
}
