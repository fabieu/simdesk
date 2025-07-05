package de.sustineo.simdesk.client.events;

import de.sustineo.simdesk.client.AccBroadcastingThread;
import de.sustineo.simdesk.eventbus.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class ConnectionClosedEvent extends Event {
    private final AccBroadcastingThread.ExitState exitState;
}
