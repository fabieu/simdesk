
package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.protocol.RealtimeInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class RealtimeCarUpdateEvent extends Event {
    private final RealtimeInfo realtimeInfo;
}
