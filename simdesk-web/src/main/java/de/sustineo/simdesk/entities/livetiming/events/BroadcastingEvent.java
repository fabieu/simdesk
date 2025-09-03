package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.BroadcastingInfo;

public class BroadcastingEvent extends LiveTimingEvent<BroadcastingInfo> {
    public BroadcastingEvent(String dashboardId, BroadcastingInfo broadcastingInfo) {
        super(dashboardId, broadcastingInfo);
    }
}