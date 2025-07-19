package de.sustineo.simdesk.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Track {
    IMOLA("Autodromo Enzo e Dino Ferrari", 44.340278, 11.713611, "imola"),
    MONZA("Autodromo Nazionale Monza", 45.61896, 9.281216, "monza"),
    BRANDS_HATCH("Brands Hatch", 51.359444, 0.260556, "brands_hatch"),
    PAUL_RICARD("Circuit Paul Ricard", 43.250556, 5.791667, "paul_ricard"),
    VALENCIA("Circuit Ricardo Tormo", 39.485833, -0.628056, "valencia"),
    ZANDVOORT("Circuit Zandvoort", 52.388056, 4.544444, "zandvoort"),
    ZOLDER("Circuit Zolder", 50.989422, 5.25705, "zolder"),
    BARCELONA("Circuit de Barcelona-Catalunya", 41.57, 2.258056, "barcelona"),
    COTA("Circuit of the Americas", 30.136611, -97.630692, "cota"),
    DONINGTON("Donington Park", 52.830556, -1.375278, "donington"),
    HUNGARORING("Hungaroring", 47.583056, 19.251111, "hungaroring"),
    INDIANAPOLIS("Indianapolis Motor Speedway", 39.794853, -86.234822, "indianapolis"),
    KYALAMI("Kyalami Grand Prix Circuit", -25.998779, 28.069907, "kyalami"),
    LAGUNA_SECA("Laguna Seca Raceway", 36.584722, -121.752778, "laguna_seca"),
    MISANO("Misano World Circuit Marco Simoncelli", 43.96138, 12.6833339, "misano"),
    MOUNT_PANORAMA("Mount Panorama Circuit", -33.4475, 149.556389, "mount_panorama"),
    NURBURGRING_24H("Nürburgring 24h", 50.353248, 6.948595, "nurburgring_24h"),
    NURBURGRING("Nürburgring", 50.33174, 6.941024, "nurburgring"),
    OULTON_PARK("Oulton Park", 53.177594, -2.614378, "oulton_park"),
    RED_BULL_RING("RedBull Ring", 47.219722, 14.764722, "red_bull_ring"),
    SILVERSTONE("Silverstone Circuit", 52.070278, -1.016667, "silverstone"),
    SNETTERTON("Snetterton Motor Racing Circuit", 52.466389, 0.945833, "snetterton"),
    SPA("Spa-Francorchamps", 50.438056, 5.969722, "spa"),
    SUZUKA("Suzuka International Racing Course", 34.844444, 136.533333, "suzuka"),
    WATKINS_GLEN("Watkins Glen International", 42.336944, -76.927222, "watkins_glen");

    private final String name;
    private final double latitude;
    private final double longitude;
    private final String accId;

    private static final Map<String, Track> ACC_TRACKS = Stream.of(values())
            .collect(Collectors.toMap(Track::getAccId, track -> track));

    /**
     * Checks if a track exists by its ACC identifier.
     */
    public static boolean existsInAcc(String trackId) {
        return ACC_TRACKS.containsKey(trackId);
    }

    /**
     * Retrieves a Track enum by its ACC identifier.
     * Returns null if not found.
     */
    public static Track getByAccId(String accId) {
        return ACC_TRACKS.get(accId);
    }

    /**
     * Retrieves the track name by ACC identifier or returns a default.
     */
    public static String getTrackNameByAccId(String accId) {
        return Optional.ofNullable(getByAccId(accId))
                .map(Track::getName)
                .orElse(Constants.UNKNOWN);
    }

    /**
     * Returns all tracks sorted by their display name.
     */
    public static List<Track> getAllSortedByNameForAcc() {
        return Stream.of(values())
                .sorted(Comparator.comparing(Track::getName))
                .collect(Collectors.toList());
    }
}
