
package de.sustineo.simdesk.entities.events;

import de.sustineo.simdesk.eventbus.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class ConnectionOpenedEvent extends Event {
}
