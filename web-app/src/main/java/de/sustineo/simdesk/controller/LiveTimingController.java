package de.sustineo.simdesk.controller;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.services.livetiming.LiveTimingProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Profile(ProfileManager.PROFILE_LIVE_TIMING)
@Log
@Controller
@RequiredArgsConstructor
public class LiveTimingController {
    private final LiveTimingProcessor liveTimingProcessor;

    @MessageMapping("/acc/live-timing")
    public void handleLiveTimingUpdate(@Payload byte[] payload,
                                       @Header("dashboard-id") String dashboardId,
                                       SimpMessageHeaderAccessor headerAccessor) {
        liveTimingProcessor.processMessage(headerAccessor.getSessionId(), dashboardId, payload);
    }
}
