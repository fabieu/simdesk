package de.sustineo.simdesk.services.livetiming;

import de.sustineo.simdesk.configuration.ProfileManager;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Profile(ProfileManager.PROFILE_LIVE_TIMING)
@Log
@Service
@RequiredArgsConstructor
public class LiveTimingRequestService {
    private static final String SOCKET_QUEUE_ACC_REQUEST = "/queue/acc/request";

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendEntrylistRequest(@Nullable String sessionId, @Nullable Integer connectionId) {
        if (sessionId == null || connectionId == null) {
            return;
        }

        byte[] message = buildEntryListRequest(connectionId);
        simpMessagingTemplate.convertAndSendToUser(sessionId, SOCKET_QUEUE_ACC_REQUEST, message, createHeaders(sessionId));
    }

    public void sendTrackDataRequest(@Nullable String sessionId, @Nullable Integer connectionId) {
        if (sessionId == null || connectionId == null) {
            return;
        }

        byte[] message = buildTrackDataRequest(connectionId);
        simpMessagingTemplate.convertAndSendToUser(sessionId, SOCKET_QUEUE_ACC_REQUEST, message, createHeaders(sessionId));
    }

    private MessageHeaders createHeaders(@Nullable String sessionId) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        accessor.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM);
        accessor.setSessionId(sessionId);
        accessor.setLeaveMutable(true);
        return accessor.getMessageHeaders();
    }

    private byte[] buildEntryListRequest(int connectionId) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.REQUEST_ENTRY_LIST);
        message.writeBytes(toByteArray(connectionId));
        return message.toByteArray();
    }

    private byte[] buildTrackDataRequest(int connectionId) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.REQUEST_TRACK_DATA);
        message.writeBytes(toByteArray(connectionId));
        return message.toByteArray();
    }

    private byte[] toByteArray(int value) {
        return ByteBuffer.allocate(4) // 4 bytes = size of an int (32 bits)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(value)
                .array();
    }

    private interface OutboundMessageTypes {
        byte REQUEST_ENTRY_LIST = 0x0A;
        byte REQUEST_TRACK_DATA = 0x0B;
    }
}
