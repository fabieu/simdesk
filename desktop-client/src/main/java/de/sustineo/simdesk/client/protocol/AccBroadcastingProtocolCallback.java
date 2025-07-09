
package de.sustineo.simdesk.client.protocol;

import java.util.List;


public interface AccBroadcastingProtocolCallback {

    void onPacketReceived(byte[] payload);

    void onRegistrationResult(int connectionID, boolean success, boolean readOnly, String message);

    void onRealtimeUpdate(SessionInfo sessionInfo);

    void onRealtimeCarUpdate(RealtimeInfo info);

    void onEntryListUpdate(List<Integer> carIds);

    void onTrackData(TrackInfo info);

    void onEntryListCarUpdate(CarInfo carInfo);

    void onBroadcastingEvent(BroadcastingEvent event);
}
