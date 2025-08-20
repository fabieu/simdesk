package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.CarInfo;

public class CarConnectedEvent extends LiveTimingEvent<CarInfo> {
    public CarConnectedEvent(CarInfo carInfo, String dashboardId) {
        super(carInfo, dashboardId);
    }
}
