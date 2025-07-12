package de.sustineo.simdesk.entities.livetiming.protocol.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DriverCategory {
    BRONZE(0, "Bronze"),
    SILVER(1, "Silver"),
    GOLD(2, "Gold"),
    PLATINUM(3, "Platinum"),
    ERROR(255, "Error");

    private final int id;
    private final String text;

    public static DriverCategory fromId(int id) {
        return switch (id) {
            case 0 -> BRONZE;
            case 1 -> SILVER;
            case 2 -> GOLD;
            case 3 -> PLATINUM;
            default -> ERROR;
        };
    }
}
