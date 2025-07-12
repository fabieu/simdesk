
package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.protocol.SessionId;
import de.sustineo.simdesk.entities.livetiming.protocol.SessionInfo;
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
