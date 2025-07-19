
package de.sustineo.simdesk.entities.json.kunos.acc.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccSessionPhase {
    NONE(0),
    STARTING(1),
    PRE_FORMATION(2),
    FORMATION_LAP(3),
    PRE_SESSION(4),
    SESSION(5),
    SESSION_OVER(6),
    POST_SESSION(7),
    RESULT_UI(8);

    private final int id;

    public static AccSessionPhase getById(int id) {
        for (AccSessionPhase accSessionPhase : AccSessionPhase.values()) {
            if (accSessionPhase.id == id) {
                return accSessionPhase;
            }
        }

        return NONE;
    }

    public static AccSessionPhase getNext(AccSessionPhase phase) {
        if (phase == RESULT_UI) {
            return phase;
        }

        return AccSessionPhase.values()[phase.ordinal() + 1];
    }

}
