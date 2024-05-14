package de.sustineo.simdesk.entities;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum DriverCategory {
    Bronze(0),
    Silver(1),
    Gold(2),
    Platinum(3);

    private final int id;
    private static final Map<Integer, DriverCategory> map = new HashMap<>();

    DriverCategory(int id) {
        this.id = id;
    }

    static {
        for (DriverCategory driverCategory : DriverCategory.values()) {
            map.put(driverCategory.id, driverCategory);
        }
    }

    public static DriverCategory valueOf(int id) {
        return map.get(id);
    }
}
