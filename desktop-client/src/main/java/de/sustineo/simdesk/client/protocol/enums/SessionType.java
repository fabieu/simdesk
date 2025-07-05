package de.sustineo.simdesk.client.protocol.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SessionType {
    PRACTICE(0),
    QUALIFYING(4),
    SUPERPOLE(9),
    RACE(10),
    HOTLAP(11),
    HOTSTINT(12),
    HOTLAPSUPERPOLE(13),
    REPLAY(14),
    NONE(255);

    private final int id;

    public static SessionType fromId(int id) {
        return switch (id) {
            case 0 -> PRACTICE;
            case 4 -> QUALIFYING;
            case 9 -> SUPERPOLE;
            case 10 -> RACE;
            case 11 -> HOTLAP;
            case 12 -> HOTSTINT;
            case 13 -> HOTLAPSUPERPOLE;
            case 14 -> REPLAY;
            default -> NONE;
        };
    }
}
