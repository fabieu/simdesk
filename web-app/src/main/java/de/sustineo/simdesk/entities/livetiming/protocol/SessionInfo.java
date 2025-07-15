
package de.sustineo.simdesk.entities.livetiming.protocol;

import de.sustineo.simdesk.entities.livetiming.protocol.enums.SessionPhase;
import de.sustineo.simdesk.entities.livetiming.protocol.enums.SessionType;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionInfo {
    private int eventIndex;
    private int sessionIndex;
    @Builder.Default
    private SessionType sessionType = SessionType.NONE;
    @Builder.Default
    private SessionPhase phase = SessionPhase.NONE;
    private int sessionTime;
    private int sessionEndTime;
    private int focusedCarIndex;
    @Builder.Default
    private String activeCameraSet = "";
    @Builder.Default
    private String activeCamera = "";
    @Builder.Default
    private String currentHudPage = "";
    private boolean replayPlaying;
    private int replaySessionTime;
    private int replayRemainingTime;
    private int timeOfDay;
    private byte ambientTemp;
    private byte trackTemp;
    private byte cloudLevel;
    private byte rainLevel;
    private byte wetness;
    @Builder.Default
    private LapInfo bestSessionLap = new LapInfo();
}
