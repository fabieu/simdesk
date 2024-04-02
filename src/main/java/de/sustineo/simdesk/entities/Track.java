package de.sustineo.simdesk.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Track extends Entity {
    private static final HashMap<String, Track> tracks = new HashMap<>();
    private final String trackId;
    private final String trackName;


    static {
        addTrack("monza", "Monza");
        addTrack("zolder", "Zolder");
        addTrack("brands_hatch", "Brands Hatch");
        addTrack("silverstone", "Silverstone");
        addTrack("paul_ricard", "Paul Ricard");
        addTrack("misano", "Misano");
        addTrack("spa", "Spa Franchorchamp");
        addTrack("nurburgring", "Nürburgring");
        addTrack("barcelona", "Barcelona");
        addTrack("hungaroring", "Hungaroring");
        addTrack("zandvoort", "Zandvoort");
        addTrack("kyalami", "Kyalami");
        addTrack("mount_panorama", "Mount Panorama");
        addTrack("suzuka", "Suzuka");
        addTrack("laguna_seca", "Laguna Seca");
        addTrack("imola", "Imola");
        addTrack("oulton_park", "Oulton Park");
        addTrack("donington", "Donington");
        addTrack("snetterton", "Snetterton");
        addTrack("cota", "Circuit of the Americas");
        addTrack("indianapolis", "Indianapolis");
        addTrack("watkins_glen", "Watkins Glen");
        addTrack("valencia", "Valencia");
        addTrack("red_bull_ring", "RedBull Ring");
        addTrack("nurburgring_24h", "Nürburgring 24h");
    }

    private static void addTrack(String trackId, String trackName) {
        tracks.put(trackId, new Track(trackId, trackName));
    }

    public static boolean isValid(String trackId) {
        return tracks.containsKey(trackId);
    }

    public static String getTrackNameById(String trackId) {
        return Optional.ofNullable(tracks.get(trackId))
                .map(Track::getTrackName)
                .orElse(UNKNOWN);
    }

    public static List<Track> getAllSortedByName() {
        return tracks.values().stream()
                .sorted(Comparator.comparing(Track::getTrackName))
                .collect(Collectors.toList());
    }
}
