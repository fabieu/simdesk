package de.sustineo.simdesk.entities.stewarding;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Series {
    private String id;
    private String title;
    private String description;
    private String discordWebhookUrl;
    private Boolean videoUrlEnabled;
    private String penaltyCatalogId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Instant createdAt;
    private Instant updatedAt;

    @EqualsAndHashCode.Exclude
    private PenaltyCatalog penaltyCatalog;
}
