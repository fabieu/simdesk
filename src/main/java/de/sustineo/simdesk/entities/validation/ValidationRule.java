package de.sustineo.simdesk.entities.validation;

import lombok.Getter;

@Getter
public enum ValidationRule {
    RACE_NUMBERS_INVALID("validateRaceNumbers", "Invalid race numbers", "Check if race numbers are valid, also check for duplicate race numbers."),
    STEAM_IDS_INVALID("validateSteamIDs", "Invalid SteamIDs", "Check if SteamIDs are present, also check for duplicate SteamIDs."),
    GRID_POSITIONS_INVALID("validateGridPositions", "Invalid grid positions", "Check if grid positions are valid, also check for duplicate grid positions."),
    BALLAST_INVALID("validateBallast", "Invalid ballast values", "Check if ballast values are inside the allowed threshold."),
    RESTRICTOR_INVALID("validateRestrictor", "Invalid restrictor values", "Check if restrictor values are inside the allowed threshold."),
    DRIVER_NAMES_MISSING("validateDriverNames", "Missing driver names", "Check if firstName, lastName and shortName are set if overrideDriverInfo is set to 1."),
    DRIVER_CATEGORIES_INVALID("validateDriverCategories", "Invalid driver categories", "Check if driver categories are valid if overrideDriverInfo is set to 1.");

    private final String methodName;
    private final String friendlyName;
    private final String description;


    ValidationRule(String methodName, String friendlyName, String description) {
        this.methodName = methodName;
        this.friendlyName = friendlyName;
        this.description = description;
    }
}
