
package de.sustineo.simdesk.entities.livetiming.protocol.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BroadcastingEventType {
    NONE(0),
    GREEN_FLAG(1),
    SESSION_OVER(2),
    PENALTY_COMM_MSG(3),
    ACCIDENT(4),
    LAP_COMPLETED(5),
    BEST_SESSION_LAP(6),
    BEST_PERSONAL_LAP(7);

    private final int id;

    public static BroadcastingEventType fromId(int id) {
        return switch (id) {
            case 1 -> GREEN_FLAG;
            case 2 -> SESSION_OVER;
            case 3 -> PENALTY_COMM_MSG;
            case 4 -> ACCIDENT;
            case 5 -> LAP_COMPLETED;
            case 6 -> BEST_SESSION_LAP;
            case 7 -> BEST_PERSONAL_LAP;
            default -> NONE;
        };
    }
}
