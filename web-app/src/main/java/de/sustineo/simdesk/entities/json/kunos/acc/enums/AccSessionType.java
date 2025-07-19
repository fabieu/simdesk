package de.sustineo.simdesk.entities.json.kunos.acc.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccSessionType {
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

    public static AccSessionType fromId(int id) {
        for (AccSessionType sessionType : AccSessionType.values()) {
            if (sessionType.id == id) {
                return sessionType;
            }
        }

        return NONE;
    }
}
