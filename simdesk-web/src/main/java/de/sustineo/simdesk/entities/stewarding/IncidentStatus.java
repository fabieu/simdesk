package de.sustineo.simdesk.entities.stewarding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IncidentStatus {
    REPORTED("Reported"),
    UNDER_REVIEW("Under Review"),
    DECISION_MADE("Decision Made"),
    APPEALED("Appealed"),
    APPEAL_REVIEWED("Appeal Reviewed"),
    CLOSED("Closed");

    private final String description;
}
