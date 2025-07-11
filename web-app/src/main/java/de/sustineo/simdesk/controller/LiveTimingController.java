package de.sustineo.simdesk.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Log
@Controller
@RequiredArgsConstructor
public class LiveTimingController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/livetiming")
    public void handleLiveTimingUpdate(@Header(name = "dashboardId") String dashboardId, @Payload byte[] data) {
        log.info("Received livetiming payload from dashboardId: " + dashboardId + ", data: " + new String(data));
        // Publish the live timing data to the WebSocket topic for the specific session
        //simpMessagingTemplate.convertAndSend("/user/queue/acc.request" + dashboardId, new String(data));
    }
}
