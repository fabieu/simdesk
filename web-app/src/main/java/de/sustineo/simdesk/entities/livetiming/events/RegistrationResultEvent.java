
package de.sustineo.simdesk.entities.livetiming.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class RegistrationResultEvent extends Event {
    private final int connectionId;
    private final boolean success;
    private final boolean readOnly;
    private final String message;
}
