
package de.sustineo.simdesk.client.events;

import de.sustineo.simdesk.client.protocol.CarInfo;
import de.sustineo.simdesk.eventbus.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class EntryListCarUpdateEvent extends Event {
    private final CarInfo carInfo;
}
