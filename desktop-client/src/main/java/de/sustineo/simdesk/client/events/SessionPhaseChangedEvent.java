
package de.sustineo.simdesk.client.events;

import de.sustineo.simdesk.client.protocol.SessionInfo;
import de.sustineo.simdesk.eventbus.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class SessionPhaseChangedEvent extends Event {
    private final SessionInfo sessionInfo;
    private final boolean initialisation;
}
