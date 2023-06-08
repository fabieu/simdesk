package de.sustineo.acc.leaderboard.entities;

import java.util.HashMap;

public class Track {
    private static final HashMap<String, String> tracks = new HashMap<>();
    private static final String DEFAULT_TRACK_NAME = "Unknown";

    static {
    }

    public static String getTrackNameById(String trackId) {
        return tracks.getOrDefault(trackId, DEFAULT_TRACK_NAME);
    }
}
