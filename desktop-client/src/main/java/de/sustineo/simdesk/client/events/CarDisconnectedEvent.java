
package de.sustineo.simdesk.client.events;

import de.sustineo.simdesk.client.model.Car;
import de.sustineo.simdesk.eventbus.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class CarDisconnectedEvent extends Event {
    private final Car car;
}
