package de.sustineo.simdesk.entities.livetiming.model;

import de.sustineo.simdesk.entities.livetiming.protocol.SessionInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class Session {
    public SessionInfo raw = new SessionInfo();
    public List<Integer> sessionBestSplits = Arrays.asList(
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            Integer.MAX_VALUE
    );
    public int maxKMH;
    public int maxSpeedTrapKMH;

    public synchronized Session copy() {
        Session session = new Session();
        session.raw = this.raw;
        session.sessionBestSplits = new ArrayList<>(this.sessionBestSplits);
        session.maxKMH = this.maxKMH;
        session.maxSpeedTrapKMH = this.maxSpeedTrapKMH;
        return session;
    }
}