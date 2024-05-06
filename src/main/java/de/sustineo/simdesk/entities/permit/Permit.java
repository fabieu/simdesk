package de.sustineo.simdesk.entities.permit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permit {
    private Long userId;
    private String permit;
    private Instant updateDatetime;
}
