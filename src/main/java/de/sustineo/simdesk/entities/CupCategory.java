package de.sustineo.simdesk.entities;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum CupCategory {
    Overall(0),
    ProAm(1),
    Am(2),
    Silver(3),
    National(4);

    private final int id;
    private static final Map<Integer, CupCategory> map = new HashMap<>();

    CupCategory(int id) {
        this.id = id;
    }

    static {
        for (CupCategory cupCategory : CupCategory.values()) {
            map.put(cupCategory.id, cupCategory);
        }
    }

    public static CupCategory valueOf(int id) {
        return map.get(id);
    }
}
