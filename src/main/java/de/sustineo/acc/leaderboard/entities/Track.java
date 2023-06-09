package de.sustineo.acc.leaderboard.entities;

import java.util.HashMap;

public class Track {
    private static final HashMap<String, String> tracks = new HashMap<>();
    private static final String DEFAULT_TRACK_NAME = "Unknown";

    static {
        tracks.put("monza", "Monza");
        tracks.put("zolder", "Zolder");
        tracks.put("brands_hatch", "Brands Hatch");
        tracks.put("silverstone", "Silverstone");
        tracks.put("paul_richard", "Paul Richard");
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
    }

    public static boolean isTrackIdValid(String trackId) {
        return tracks.containsKey(trackId);
    }

    public static String getTrackNameById(String trackId) {
        return tracks.getOrDefault(trackId, DEFAULT_TRACK_NAME);
    }
}
