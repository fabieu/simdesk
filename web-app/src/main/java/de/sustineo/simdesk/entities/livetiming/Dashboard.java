package de.sustineo.simdesk.entities.livetiming;

import de.sustineo.simdesk.entities.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class Dashboard {
    @NotBlank(message = "ID is required")
    private String id;
    @NotNull(message = "Visibility is required")
    private Visibility visibility;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private String broadcastUrl;
    private Instant startDatetime;
    private Instant endDatetime;
    private DashboardState state;
    private Instant stateDatetime;
    private Instant updateDatetime;
    private Instant createDatetime;

    public Dashboard(String id) {
        this.id = id;
    }
}
