package de.sustineo.simdesk.services.livetiming;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Log
@Service
@RequiredArgsConstructor
public class LiveTimingService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void handleLivetimingEvent(String sessionId, String dashboardId, byte[] data) {
        log.info("Handle livetiming payload from dashboardId: " + dashboardId + ", data: " + new String(data));

        simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/acc/socket.request", "This should be a response to the client".getBytes(StandardCharsets.UTF_8));
    }
}
