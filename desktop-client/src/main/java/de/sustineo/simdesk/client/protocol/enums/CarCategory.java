
package de.sustineo.simdesk.client.protocol.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarCategory {
    GT3("GT3"),
    GT4("GT4"),
    GT2("GT2"),
    ST("ST"),
    ST22("ST"),
    CUP("CUP"),
    CUP21("CUP"),
    CHL("CHL"),
    TCX("TCX"),
    NONE("None");

    private final String text;
}
