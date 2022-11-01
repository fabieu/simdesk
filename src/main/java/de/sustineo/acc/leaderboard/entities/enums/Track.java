package de.sustineo.acc.leaderboard.entities.enums;

import lombok.Getter;

@SuppressWarnings("unused")
@Getter
public enum Track {
    monza("Monza"),
    zolder("Zolder"),
    brands_hatch("Brands Hatch"),
    silverstone("Silverstone"),
    paul_richard("Paul Richard"),
    misano("Misano"),
    spa("Spa Franchorchamp"),
    nurburgring("Nürburgring"),
    barcelona("Barcelona"),
    hungaroring("Hungaroring"),
    zandvoort("Zandvoort"),
    kyalami("Kyalami"),
    mount_panorama("Mount Panorama"),
    suzuka("Suzuka"),
    laguna_seca("Laguna Seca"),
    imola("Imola"),
    oulton_park("Oulton Park"),
    donington("Donington"),
    snetterton("Snetterton"),
    cota("Circuit of the Americas"),
    indianapolis("Indianapolis"),
    watkins_glen("Watkins Glen");

    private final String displayName;

    Track(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.getDisplayName();
    }
}
