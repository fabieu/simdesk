
package de.sustineo.simdesk.entities.json.kunos.acc.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccCarLocation {
    NONE(0),
    TRACK(1),
    PIT_LANE(2),
    PIT_ENTRY(3),
    PIT_EXIT(4);

    private final int id;

    public static AccCarLocation getById(int id) {
        for (AccCarLocation accCarLocation : AccCarLocation.values()) {
            if (accCarLocation.id == id) {
                return accCarLocation;
            }
        }

        return NONE;
    }
}
