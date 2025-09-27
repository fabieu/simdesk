package de.sustineo.simdesk.entities;

import de.sustineo.simdesk.views.filter.GridEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum SessionType implements GridEnum {
    FP("Free Practice"),
    Q("Qualifying"),
    R("Race"),
    UNKNOWN("Unknown");

    private final String description;

    public static Set<SessionType> getValid() {
        EnumSet<SessionType> set = EnumSet.allOf(SessionType.class);
        set.remove(UNKNOWN);
        return set;
    }

    @Override
    public String getLabel() {
        return description;
    }
}
