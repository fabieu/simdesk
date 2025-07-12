package de.sustineo.simdesk.services.livetiming;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Log
@Service
@RequiredArgsConstructor
public class LiveTimingService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void handleLivetimingEvent(String sessionId, String dashboardId, byte[] data) {
        log.fine("Handle livetiming payload from dashboardId: " + dashboardId + ", data: " + new String(data));

        //simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/acc/request", "This should be a response to the client", createHeaders(sessionId));
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        accessor.setSessionId(sessionId);
        accessor.setLeaveMutable(true);
        return accessor.getMessageHeaders();
    }
}
