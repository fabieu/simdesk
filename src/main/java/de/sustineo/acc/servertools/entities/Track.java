package de.sustineo.acc.servertools.entities;

import java.util.HashMap;

public class Track extends Entity {
    private static final HashMap<String, String> tracks = new HashMap<>();

    static {
        tracks.put("monza", "Monza");
        tracks.put("zolder", "Zolder");
        tracks.put("brands_hatch", "Brands Hatch");
        tracks.put("silverstone", "Silverstone");
        tracks.put("paul_ricard", "Paul Richard");
        tracks.put("misano", "Misano");
        tracks.put("spa", "Spa Franchorchamp");
        tracks.put("nurburgring", "Nürburgring");
        tracks.put("barcelona", "Barcelona");
        tracks.put("hungaroring", "Hungaroring");
        tracks.put("zandvoort", "Zandvoort");
        tracks.put("kyalami", "Kyalami");
        tracks.put("mount_panorama", "Mount Panorama");
        tracks.put("suzuka", "Suzuka");
        tracks.put("laguna_seca", "Laguna Seca");
        tracks.put("imola", "Imola");
        tracks.put("oulton_park", "Oulton Park");
        tracks.put("donington", "Donington");
        tracks.put("snetterton", "Snetterton");
        tracks.put("cota", "Circuit of the Americas");
        tracks.put("indianapolis", "Indianapolis");
        tracks.put("watkins_glen", "Watkins Glen");
        tracks.put("valencia", "Valencia");
        tracks.put("red_bull_ring", "RedBull Ring");
    }

    public static boolean isValid(String trackId) {
        return tracks.containsKey(trackId.toLowerCase());
    }

    public static String getTrackNameById(String trackId) {
        return tracks.getOrDefault(trackId, UNKNOWN);
    }
}
