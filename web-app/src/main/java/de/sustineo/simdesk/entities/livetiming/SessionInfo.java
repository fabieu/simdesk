
package de.sustineo.simdesk.entities.livetiming;

import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccSessionPhase;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccSessionType;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionInfo {
    private int eventIndex;
    private int sessionIndex;
    @Builder.Default
    private AccSessionType sessionType = AccSessionType.NONE;
    @Builder.Default
    private AccSessionPhase phase = AccSessionPhase.NONE;
    private int sessionTime;
    private int sessionEndTime;
    private int focusedCarIndex;
    private String activeCameraSet;
    private String activeCamera;
    private String currentHudPage;
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
