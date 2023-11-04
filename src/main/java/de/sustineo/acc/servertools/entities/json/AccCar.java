package de.sustineo.acc.servertools.entities.json;

import de.sustineo.acc.servertools.entities.enums.CarGroup;
import de.sustineo.acc.servertools.entities.enums.CupCategory;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class AccCar {
    private Integer carId;
    private Integer raceNumber;
    private Integer carModel;
    private CupCategory cupCategory;
    private CarGroup carGroup;
    private String teamName;
    private Integer nationality;
    private Integer carGuid;
    private Integer teamGuid;
    private Integer ballastKg;
    private List<AccDriver> drivers;

    public Optional<AccDriver> getDriverByIndex(int index) {
        return Optional.ofNullable(drivers.get(index));
    }

    /**
     * Override car group based on car model id, because Kunos can't set the car group correctly.
     *
     * @return Corrected car group
     */
    public CarGroup getCarGroup() {
        if (carModel == null) {
            return CarGroup.UNKNOWN;
        }

        if (carModel >= 50 && carModel <= 61) {
            return CarGroup.GT4;
        }

        if (carModel == 27) {
            return CarGroup.TCX;
        }

        if (carModel == 9 || carModel == 28) {
            return CarGroup.CUP;
        }

        if (carModel == 18 || carModel == 29) {
            return CarGroup.ST;
        }

        if (carModel == 26) {
            return CarGroup.CHL;
        }

        return CarGroup.GT3;
    }
}
