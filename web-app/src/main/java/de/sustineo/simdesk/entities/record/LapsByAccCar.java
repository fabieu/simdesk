package de.sustineo.simdesk.entities.record;

import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;

import java.util.List;

public record LapsByAccCar(AccCar car, List<Lap> laps) {
    public static LapsByAccCar of(AccCar car, List<Lap> laps) {
        return new LapsByAccCar(car, laps);
    }
}
