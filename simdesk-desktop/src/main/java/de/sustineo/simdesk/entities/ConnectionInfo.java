package de.sustineo.simdesk.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ConnectionInfo {
    @JsonProperty(value = "updListenerPort")
    private int port;

    @JsonProperty(value = "connectionPassword")
    private String connectionPassword;

    @JsonProperty(value = "commandPassword")
    private String commandPassword;
}
