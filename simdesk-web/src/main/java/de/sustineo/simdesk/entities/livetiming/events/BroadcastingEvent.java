package de.sustineo.simdesk.entities.livetiming.events;

import de.sustineo.simdesk.entities.livetiming.BroadcastingInfo;

public class BroadcastingEvent extends LiveTimingEvent<BroadcastingInfo> {
    public BroadcastingEvent(BroadcastingInfo broadcastingInfo, String dashboardId) {
        super(broadcastingInfo, dashboardId);
    }
}