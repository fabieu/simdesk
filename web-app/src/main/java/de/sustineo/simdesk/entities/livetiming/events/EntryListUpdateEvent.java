
package de.sustineo.simdesk.entities.livetiming.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ToString
public class EntryListUpdateEvent extends Event {
    private final List<Integer> carIds;
}
