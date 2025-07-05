
package de.sustineo.simdesk.client.protocol;

import de.sustineo.simdesk.client.protocol.enums.CarLocation;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RealtimeInfo {
    @Builder.Default
    private final boolean isDefault = true;
    private int carId;
    private int driverIndex;
    private byte driverCount;
    private byte gear;
    private float yaw;
    private float pitch;
    private float roll;
    @Builder.Default
    private CarLocation location = CarLocation.NONE;
    private int kmh;
    private int position;
    private int cupPosition;
    private int trackPosition;
    private float splinePosition;
    private int laps;
    private int delta;
    @Builder.Default
    private LapInfo bestSessionLap = new LapInfo();
    @Builder.Default
    private LapInfo lastLap = new LapInfo();
    @Builder.Default
    private LapInfo currentLap = new LapInfo();
}
