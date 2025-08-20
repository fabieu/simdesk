package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.CarInfo;

public class CarEvent extends LiveTimingEvent<CarInfo> {
    public CarEvent(CarInfo carInfo, String dashboardId) {
        super(carInfo, dashboardId);
    }
}
