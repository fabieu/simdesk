package de.sustineo.simdesk.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
public class Track extends Entity {
    private static final HashMap<String, Track> tracks = new HashMap<>();

    private final String accId;
    private final String name;
    private final double latitude;
    private final double longitude;

    public Track(String name, double latitude, double longitude, String accId) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accId = accId;
    }

    static {
        addTrack("Autodromo Nazionale Monza", 45.61896, 9.281216, "monza");
        addTrack("Circuit Zolder", 50.989422, 5.25705, "zolder");
        addTrack("Brands Hatch", 51.359444, 0.260556, "brands_hatch");
        addTrack("Silverstone Circuit", 52.070278, -1.016667, "silverstone");
        addTrack("Circuit Paul Ricard", 43.250556, 5.791667, "paul_ricard");
        addTrack("Misano World Circuit Marco Simoncelli", 43.96138, 12.6833339, "misano");
        addTrack("Spa-Francorchamps", 50.438056, 5.969722, "spa");
        addTrack("Nürburgring", 50.346111, 6.966111, "nurburgring");
        addTrack("Circuit de Barcelona-Catalunya", 41.57, 2.258056, "barcelona");
        addTrack("Hungaroring", 47.583056, 19.251111, "hungaroring");
        addTrack("Circuit Zandvoort", 52.388056, 4.544444, "zandvoort");
        addTrack("Kyalami Grand Prix Circuit", -25.998779, 28.069907, "kyalami");
        addTrack("Mount Panorama Circuit", -33.4475, 149.556389, "mount_panorama");
        addTrack("Suzuka International Racing Course", 34.844444, 136.533333, "suzuka");
        addTrack("Laguna Seca Raceway", 36.584722, -121.752778, "laguna_seca");
        addTrack("Autodromo Enzo e Dino Ferrari", 44.340278, 11.713611, "imola");
        addTrack("Oulton Park", 53.177594, -2.614378, "oulton_park");
        addTrack("Donington Park", 52.830556, -1.375278, "donington");
        addTrack("Snetterton Motor Racing Circuit", 52.466389, 0.945833, "snetterton");
        addTrack("Circuit of the Americas", 30.136611, -97.630692, "cota");
        addTrack("Indianapolis Motor Speedway", 39.794853, -86.234822, "indianapolis");
        addTrack("Watkins Glen International", 42.336944, -76.927222, "watkins_glen");
        addTrack("Circuit Ricardo Tormo", 39.485833, -0.628056, "valencia");
        addTrack("RedBull Ring", 47.219722, 14.764722, "red_bull_ring");
        addTrack("Nürburgring 24h", 50.346111, 6.966111, "nurburgring_24h");
    }

    private static void addTrack(String trackName, Double latitude, Double longitude, String accId) {
        tracks.put(accId, new Track(trackName, latitude, longitude, accId));
    }

    public static boolean isValid(String trackId) {
        return tracks.containsKey(trackId);
    }

    public static Track getTrackById(String trackId) {
        return tracks.get(trackId);
    }

    public static String getTrackNameById(String trackId) {
        return Optional.ofNullable(tracks.get(trackId))
                .map(Track::getName)
                .orElse(UNKNOWN);
    }

    public static List<Track> getAllSortedByName() {
        return tracks.values().stream()
                .sorted(Comparator.comparing(Track::getName))
                .collect(Collectors.toList());
    }
}
