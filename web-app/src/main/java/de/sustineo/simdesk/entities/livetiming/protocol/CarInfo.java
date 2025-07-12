package de.sustineo.simdesk.entities.livetiming.protocol;

import de.sustineo.simdesk.entities.livetiming.protocol.enums.CarModel;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CarInfo {
    @Builder.Default
    private int carId = 0;
    @Builder.Default
    private CarModel carModel = CarModel.ERROR;
    @Builder.Default
    private String teamName = "";
    @Builder.Default
    private int carNumber = 0;
    @Builder.Default
    private byte cupCategory = 0;
    @Builder.Default
    private byte currentDriverIndex = 0;
    @Builder.Default
    private int carNationality = 0;
    @Builder.Default
    private List<DriverInfo> drivers = new LinkedList<>();
    @Builder.Default
    private RealtimeInfo realtime = new RealtimeInfo();

    public String getCarNumberString() {
        return String.format("#%-3d", carNumber);
    }

    public DriverInfo getDriver() {
        if (realtime != null && drivers.size() > realtime.getDriverIndex()) {
            return drivers.get(realtime.getDriverIndex());
        } else if (drivers.size() > currentDriverIndex) {
            return drivers.get(currentDriverIndex);
        } else {
            return new DriverInfo();
        }
    }
}
