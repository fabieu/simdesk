package de.sustineo.simdesk.socket;

import lombok.*;

import java.net.InetAddress;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccSocketState {
    private InetAddress hostAddress;
    private int hostPort;
    @Builder.Default
    private String displayName = "SimDesk Live Timing";
    @Builder.Default
    private String connectionPassword = "";
    @Builder.Default
    private String commandPassword = "";
    @Builder.Default
    private int updateInterval = 500;
    @Builder.Default
    private int connectionId = -1;
    private boolean readOnly;
}