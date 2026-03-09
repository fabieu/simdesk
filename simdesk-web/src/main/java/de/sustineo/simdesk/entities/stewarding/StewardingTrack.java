package de.sustineo.simdesk.entities.stewarding;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StewardingTrack {
    private String id;
    private String name;
    private String country;
    private String mapImageUrl;
    private String mapMetadata;
    private Instant createdAt;
    private Instant updatedAt;
}
