package de.sustineo.simdesk.entities.livetiming.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class PacketReceivedEvent extends Event {
    private final byte[] payload;
}
