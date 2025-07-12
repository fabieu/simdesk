package de.sustineo.simdesk.entities.livetiming.protocol;

import lombok.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrackInfo {
    @Builder.Default
    private String trackName = "";
    @Builder.Default
    private int trackId = Integer.MAX_VALUE;
    @Builder.Default
    private int trackMeters = 5000;
    @Builder.Default
    private Map<String, List<String>> cameraSets = new HashMap<>();
    @Builder.Default
    private List<String> hudPages = new LinkedList<>();
}
