
package de.sustineo.simdesk.client.events;

import de.sustineo.simdesk.client.protocol.RealtimeInfo;
import de.sustineo.simdesk.eventbus.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class RealtimeCarUpdateEvent extends Event {
    private final RealtimeInfo realtimeInfo;
}
