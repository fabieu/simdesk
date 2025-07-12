
package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.protocol.SessionInfo;
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
