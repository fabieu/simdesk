package de.sustineo.simdesk.views.enums;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
public enum TimeRange {
    ALL_TIME("All time"),
    LAST_YEAR("Last year"),
    LAST_MONTH("Last month"),
    LAST_WEEK("Last week"),
    YEAR_2022("2022"),
    YEAR_2023("2023"),
    YEAR_2024("2024"),
    YEAR_2025("2025");

    private final String description;

    TimeRange(String description) {
        this.description = description;
    }

    public Instant from() {
        return switch (this) {
            case ALL_TIME -> Instant.EPOCH;
            case LAST_YEAR -> Instant.now().minus(365, ChronoUnit.DAYS);
            case LAST_MONTH -> Instant.now().minus(30, ChronoUnit.DAYS);
            case LAST_WEEK -> Instant.now().minus(7, ChronoUnit.DAYS);
            case YEAR_2022 -> Instant.parse("2022-01-01T00:00:00Z");
            case YEAR_2023 -> Instant.parse("2023-01-01T00:00:00Z");
            case YEAR_2024 -> Instant.parse("2024-01-01T00:00:00Z");
            case YEAR_2025 -> Instant.parse("2025-01-01T00:00:00Z");
        };
    }

    public Instant to() {
        return switch (this) {
            case YEAR_2022 -> Instant.parse("2022-12-31T23:59:59Z");
            case YEAR_2023 -> Instant.parse("2023-12-31T23:59:59Z");
            case YEAR_2024 -> Instant.parse("2024-12-31T23:59:59Z");
            case YEAR_2025 -> Instant.parse("2025-12-31T23:59:59Z");
            default -> Instant.now();
        };
    }
}
