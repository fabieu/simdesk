
package de.sustineo.simdesk.client.protocol.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SessionPhase {
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

    public static SessionPhase fromId(int id) {
        return switch (id) {
            case 1 -> STARTING;
            case 2 -> PRE_FORMATION;
            case 3 -> FORMATION_LAP;
            case 4 -> PRE_SESSION;
            case 5 -> SESSION;
            case 6 -> SESSION_OVER;
            case 7 -> POST_SESSION;
            case 8 -> RESULT_UI;
            default -> NONE;
        };
    }

    public static SessionPhase getNext(SessionPhase phase) {
        if (phase == RESULT_UI) {
            return phase;
        }
        return SessionPhase.values()[phase.ordinal() + 1];
    }

}
