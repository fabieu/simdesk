
package de.sustineo.simdesk.client.events;

import de.sustineo.simdesk.client.protocol.TrackInfo;
import de.sustineo.simdesk.eventbus.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class TrackInfoEvent extends Event {
    private final TrackInfo trackInfo;
}
