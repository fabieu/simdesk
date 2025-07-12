package de.sustineo.simdesk.socket;

import lombok.extern.java.Log;

import java.io.ByteArrayOutputStream;

@Log
public class AccSocketProtocol {
    private static final byte BROADCASTING_PROTOCOL_VERSION = 0x04;

    public static byte[] buildRegisterRequest(String name, String password, int interval, String commandPassword) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.REGISTER_COMMAND_APPLICATION);
        message.write(BROADCASTING_PROTOCOL_VERSION);
        writeString(message, name);
        writeString(message, password);
        message.write(toByteArray(interval, 4), 0, 4);
        writeString(message, commandPassword);
        return message.toByteArray();
    }

    public static byte[] buildUnregisterRequest(int connectionID) {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        message.write(OutboundMessageTypes.UNREGISTER_COMMAND_APPLICATION);
        message.write(toByteArray(connectionID, 4), 0, 4);
        return message.toByteArray();
    }

    private static void writeString(ByteArrayOutputStream outputStream, String message) {
        outputStream.write(toByteArray(message.length(), 2), 0, 2);
        outputStream.write(message.getBytes(), 0, message.length());
    }

    private static byte[] toByteArray(int n, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) (n & 0xFF);
            n = n >> 8;
        }
        return result;
    }

    public interface OutboundMessageTypes {
        byte REGISTER_COMMAND_APPLICATION = 0x01;
        byte UNREGISTER_COMMAND_APPLICATION = 0x09;
    }
}
