package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.CarInfo;

public class CarConnectedEvent extends LiveTimingEvent<CarInfo> {
    public CarConnectedEvent(String dashboardId, CarInfo carInfo) {
        super(dashboardId, carInfo);
    }
}