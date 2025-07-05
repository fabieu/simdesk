
package de.sustineo.simdesk.client.protocol.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarLocation {
    NONE(0),
    TRACK(1),
    PIT_LANE(2),
    PIT_ENTRY(3),
    PIT_EXIT(4);

    private final int id;

    public static CarLocation fromId(int id) {
        return switch (id) {
            case 1 -> TRACK;
            case 2 -> PIT_LANE;
            case 3 -> PIT_ENTRY;
            case 4 -> PIT_EXIT;
            default -> NONE;
        };
    }

}
