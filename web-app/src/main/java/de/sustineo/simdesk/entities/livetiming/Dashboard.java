package de.sustineo.simdesk.entities.livetiming;

import de.sustineo.simdesk.entities.Visibility;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Dashboard {
    private String id;
    private Visibility visibility;
    private String name;
    private String description;
    private String broadcastUrl;
    private Instant startDatetime;
    private Instant endDatetime;
    private DashboardState state;
    private Instant updateDatetime;
    private Instant createDatetime;
}
