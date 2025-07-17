package de.sustineo.simdesk.services.livetiming;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Log
@Service
@RequiredArgsConstructor
public class LiveTimingRequestService {
    private static final String SOCKET_USER_ACC_REQUEST = "/user/queue/acc/request";

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendEntrylistRequest(String sessionId, Integer connectionId) {
        byte[] message = buildEntryListRequest(connectionId);
        simpMessagingTemplate.convertAndSendToUser(sessionId, SOCKET_USER_ACC_REQUEST, message, createHeaders(sessionId));
    }

    public void sendTrackDataRequest(String sessionId, Integer connectionId) {
        byte[] message = buildTrackDataRequest(connectionId);
        simpMessagingTemplate.convertAndSendToUser(sessionId, SOCKET_USER_ACC_REQUEST, message, createHeaders(sessionId));
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        accessor.setSessionId(sessionId);
        accessor.setLeaveMutable(true);
        return accessor.getMessageHeaders();
    }

    private byte[] buildEntryListRequest(int connectionId) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.REQUEST_ENTRY_LIST);
        message.write(toByteArray(connectionId, 4), 0, 4);
        return message.toByteArray();
    }

    private byte[] buildTrackDataRequest(int connectionId) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.REQUEST_TRACK_DATA);
        message.write(toByteArray(connectionId, 4), 0, 4);
        return message.toByteArray();
    }

    private byte[] toByteArray(int n, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) (n & 0xFF);
            n = n >> 8;
        }
        return result;
    }

    private interface OutboundMessageTypes {
        byte REGISTER_COMMAND_APPLICATION = 0x01;
        byte UNREGISTER_COMMAND_APPLICATION = 0x09;
        byte REQUEST_ENTRY_LIST = 0x0A;
        byte REQUEST_TRACK_DATA = 0x0B;
        byte CHANGE_FOCUS = 0x32;
    }
}
