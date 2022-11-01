package de.sustineo.acc.leaderboard.entities.enums;

import java.util.HashMap;
import java.util.Map;

public enum Car {
    Porsche991GT3R(0, "Porsche 991 GT3 R");
    //TODO: Add all cars

    private static final Map<Integer, Car> map = new HashMap<>();
    private final int id;
    private final String displayName;


    Car(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    static {
        for (Car car : Car.values()) {
            map.put(car.id, car);
        }
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Car valueOf(int id) {
        return map.get(id);
    }
}
