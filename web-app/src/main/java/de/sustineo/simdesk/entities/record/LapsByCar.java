package de.sustineo.simdesk.entities.record;

import de.sustineo.simdesk.entities.Car;
import de.sustineo.simdesk.entities.Lap;

import java.util.List;

public record LapsByCar(Car car, List<Lap> laps) {
    public static LapsByCar of(Car car, List<Lap> laps) {
        return new LapsByCar(car, laps);
    }
}
