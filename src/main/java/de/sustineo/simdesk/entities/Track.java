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
    private static final HashMap<String, Track> accTracks = new HashMap<>();

    private String accId;
    private String name;
    private double latitude;
    private double longitude;

    public Track(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Track(String name, double latitude, double longitude, String accId) {
        this(name, latitude, longitude);
        this.accId = accId;
    }

    static {
        add("Autodromo Enzo e Dino Ferrari", 44.340278, 11.713611, "imola");
        add("Autodromo Nazionale Monza", 45.61896, 9.281216, "monza");
        add("Brands Hatch", 51.359444, 0.260556, "brands_hatch");
        add("Circuit Paul Ricard", 43.250556, 5.791667, "paul_ricard");
        add("Circuit Ricardo Tormo", 39.485833, -0.628056, "valencia");
        add("Circuit Zandvoort", 52.388056, 4.544444, "zandvoort");
        add("Circuit Zolder", 50.989422, 5.25705, "zolder");
        add("Circuit de Barcelona-Catalunya", 41.57, 2.258056, "barcelona");
        add("Circuit of the Americas", 30.136611, -97.630692, "cota");
        add("Donington Park", 52.830556, -1.375278, "donington");
        add("Hungaroring", 47.583056, 19.251111, "hungaroring");
        add("Indianapolis Motor Speedway", 39.794853, -86.234822, "indianapolis");
        add("Kyalami Grand Prix Circuit", -25.998779, 28.069907, "kyalami");
        add("Laguna Seca Raceway", 36.584722, -121.752778, "laguna_seca");
        add("Misano World Circuit Marco Simoncelli", 43.96138, 12.6833339, "misano");
        add("Mount Panorama Circuit", -33.4475, 149.556389, "mount_panorama");
        add("Nürburgring 24h", 50.353248, 6.948595, "nurburgring_24h");
        add("Nürburgring", 50.331740, 6.941024, "nurburgring");
        add("Oulton Park", 53.177594, -2.614378, "oulton_park");
        add("RedBull Ring", 47.219722, 14.764722, "red_bull_ring");
        add("Silverstone Circuit", 52.070278, -1.016667, "silverstone");
        add("Snetterton Motor Racing Circuit", 52.466389, 0.945833, "snetterton");
        add("Spa-Francorchamps", 50.438056, 5.969722, "spa");
        add("Suzuka International Racing Course", 34.844444, 136.533333, "suzuka");
        add("Watkins Glen International", 42.336944, -76.927222, "watkins_glen");
    }

    private static void add(String name, Double latitude, Double longitude, String accId) {
        accTracks.put(accId, new Track(name, latitude, longitude, accId));
    }

    public static boolean existsInAcc(String trackId) {
        return accTracks.containsKey(trackId);
    }

    public static Track getByAccId(String accId) {
        return accTracks.get(accId);
    }

    public static String getTrackNameByAccId(String accId) {
        return Optional.ofNullable(getByAccId(accId))
                .map(Track::getName)
                .orElse(UNKNOWN);
    }

    public static List<Track> getAllSortedByNameForAcc() {
        return accTracks.values().stream()
                .sorted(Comparator.comparing(Track::getName))
                .collect(Collectors.toList());
    }
}
