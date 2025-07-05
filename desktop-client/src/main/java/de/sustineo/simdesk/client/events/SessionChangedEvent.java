
package de.sustineo.simdesk.client.events;

import de.sustineo.simdesk.client.protocol.SessionId;
import de.sustineo.simdesk.client.protocol.SessionInfo;
import de.sustineo.simdesk.eventbus.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class SessionChangedEvent extends Event {
    private final SessionId sessionId;
    private final SessionInfo sessionInfo;
    private final boolean initialisation;
}
