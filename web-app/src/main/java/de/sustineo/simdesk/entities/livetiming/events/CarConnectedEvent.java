
package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.model.Car;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class CarConnectedEvent extends Event {
    private final Car car;
}
