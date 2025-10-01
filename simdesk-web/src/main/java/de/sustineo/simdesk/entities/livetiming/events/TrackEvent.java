package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.TrackInfo;

public class TrackEvent extends LiveTimingEvent<TrackInfo> {
    public TrackEvent(String dashboardId, TrackInfo trackInfo) {
        super(dashboardId, trackInfo);
    }
}