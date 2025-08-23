package de.sustineo.simdesk.entities.livetiming;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrackInfo {
    private String trackName;
    private int trackId;
    private int trackMeters;
    private Map<String, List<String>> cameraSets;
    private List<String> hudPages;
}
