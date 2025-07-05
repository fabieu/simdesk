
package de.sustineo.simdesk.client.events;

import de.sustineo.simdesk.client.protocol.BroadcastingEvent;
import de.sustineo.simdesk.eventbus.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class BroadcastingEventEvent extends Event {
    private final BroadcastingEvent event;
}
