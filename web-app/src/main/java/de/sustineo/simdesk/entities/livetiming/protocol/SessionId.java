package de.sustineo.simdesk.entities.livetiming.protocol;

import de.sustineo.simdesk.entities.livetiming.protocol.enums.SessionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Serves as a quince identifier for a session with its type, index and which number it is.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionId {
    private final SessionType type;
    private final int number;
    private final int index;

    public static SessionId of(SessionType type, int index, int number) {
        return new SessionId(type, index, number);
    }

    /**
     * Creates an uninitialised SessionId. This sessionID is invalid and should
     * be replaced as early as possible by a valid one.
     */
    public static SessionId dummy() {
        return of(SessionType.NONE, -1, 0);
    }

    public boolean isValid() {
        return (type != SessionType.NONE || index != -1 || number != 0);
    }
}
