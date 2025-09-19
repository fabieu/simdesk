package de.sustineo.simdesk.entities.bop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BopProvider {
    SELF("SimDesk", null),
    LFM("Low Fuel Motorsport", "https://lowfuelmotorsport.com/seasonsv2/bop"),
    PITSKILL("PitSkill", "https://pitskill.io/bop");

    private final String name;
    private final String reference;
}
