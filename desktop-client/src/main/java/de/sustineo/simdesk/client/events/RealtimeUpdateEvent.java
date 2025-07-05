
package de.sustineo.simdesk.client.events;

import de.sustineo.simdesk.client.protocol.SessionInfo;
import de.sustineo.simdesk.eventbus.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class RealtimeUpdateEvent extends Event {
    private final SessionInfo sessionInfo;
}
