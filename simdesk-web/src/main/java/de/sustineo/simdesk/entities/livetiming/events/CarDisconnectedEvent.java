package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.CarInfo;

public class CarDisconnectedEvent extends LiveTimingEvent<CarInfo> {
    public CarDisconnectedEvent(String dashboardId, CarInfo carInfo) {
        super(dashboardId, carInfo);
    }
}