
package de.sustineo.simdesk.entities.json.kunos.acc.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccLapType {
    ERROR(0),
    OUTLAP(1),
    REGULAR(2),
    INLAP(3);

    private final int id;

    public static AccLapType getById(int id) {
        for (AccLapType accLapType : AccLapType.values()) {
            if (accLapType.id == id) {
                return accLapType;
            }
        }

        return ERROR;
    }

}
