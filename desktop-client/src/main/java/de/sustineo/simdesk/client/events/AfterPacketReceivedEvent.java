package de.sustineo.simdesk.client.events;

import de.sustineo.simdesk.eventbus.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class AfterPacketReceivedEvent extends Event {
    private final byte type;
    private final int packageCount;
}
