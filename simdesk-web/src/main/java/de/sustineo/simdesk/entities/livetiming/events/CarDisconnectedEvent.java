package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.CarInfo;

public class CarDisconnectedEvent extends LiveTimingEvent<CarInfo> {
    public CarDisconnectedEvent(CarInfo carInfo, String dashboardId) {
        super(carInfo, dashboardId);
    }
}