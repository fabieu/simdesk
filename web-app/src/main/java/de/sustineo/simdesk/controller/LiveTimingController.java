package de.sustineo.simdesk.controller;

import de.sustineo.simdesk.services.livetiming.LiveTimingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Log
@Controller
@RequiredArgsConstructor
public class LiveTimingController {
    private final LiveTimingService liveTimingService;

    @MessageMapping("/acc/live-timing")
    public void handleLiveTimingUpdate(@Payload byte[] payload,
                                       @Header("dashboard-id") String dashboardId,
                                       SimpMessageHeaderAccessor headerAccessor) {
        liveTimingService.handleLivetimingEvent(headerAccessor.getSessionId(), dashboardId, payload);
    }
}
