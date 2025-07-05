
package de.sustineo.simdesk.client.protocol.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LapType {
    ERROR(0),
    OUTLAP(1),
    REGULAR(2),
    INLAP(3);

    private final int id;

    public static LapType fromId(int id) {
        return switch (id) {
            case 1 -> OUTLAP;
            case 2 -> REGULAR;
            case 3 -> INLAP;
            default -> ERROR;
        };
    }

}
