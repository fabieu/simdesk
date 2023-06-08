package de.sustineo.acc.leaderboard.entities;

import lombok.Data;

import java.util.HashMap;

@Data
public class Car {
    private static final HashMap<Integer, String> carModels = new HashMap<>();
    private static final String DEFAULT_CAR_NAME = "Unknown";

    static {
    }

    public static String getCarNameById(Integer carModel) {
        return carModels.getOrDefault(carModel, DEFAULT_CAR_NAME);
    }
}
